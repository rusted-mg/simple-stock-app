package io.github.rusted.simplestock.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.github.rusted.simplestock.data.ConnectionProvider;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.data.VenteRepository;
import io.github.rusted.simplestock.util.AndroidConfig;
import io.github.rusted.simplestock.util.Error;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StockViewModel extends AndroidViewModel {
    private final VenteRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<Vente>> ventes = new MutableLiveData<>();
    private final MutableLiveData<Error> error = new MutableLiveData<>();
    private final MutableLiveData<Void> venteCreated = new MutableLiveData<>();

    public StockViewModel(@NotNull Application application) {
        super(application);
        repository = new VenteRepository(new ConnectionProvider(new AndroidConfig(application.getApplicationContext())));
    }

    public MutableLiveData<Void> venteCreated() {
        return venteCreated;
    }

    public LiveData<List<Vente>> ventes() {
        return ventes;
    }

    public MutableLiveData<Error> error() {
        return error;
    }

    public void fetch() {
        executor.execute(() -> {
            try {
                List<Vente> fetched = repository.all();
                Log.i("SimpleStock", "Fetched: " + fetched.size() + " ventes");
                ventes.postValue(fetched);
            } catch (SQLException e) {
                notifyError("Failed to fetch Ventes", e);
            }
        });
    }

    public void create(Vente vente) {
        executor.execute(() -> {
            try {
                repository.insert(vente);
                venteCreated.postValue(null);
                fetch();
            } catch (SQLException e) {
                notifyError("Failed to create Vente", e);
            }
        });
    }

    private void notifyError(String message, SQLException e) {
        Log.e("SimpleStock", message, e);
        error.postValue(new Error(message, e));
    }
}
