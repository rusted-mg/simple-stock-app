package io.github.rusted.simplestock;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import io.github.rusted.simplestock.databinding.ActivityMainBinding;
import io.github.rusted.simplestock.enums.StockFormOperationMode;
import io.github.rusted.simplestock.viewmodel.StockViewModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = ((NavHostFragment) Objects.requireNonNull(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main))
        ).getNavController();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.StockFormFragment) {
                if (StockFormFragmentArgs.fromBundle(arguments).getOperation() == StockFormOperationMode.UPDATE) {
                    destination.setLabel(getString(R.string.update_vente_toolbar_label));
                } else {
                    destination.setLabel(getString(R.string.create_vente_toolbar_label));
                }
            }
        });
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        StockViewModel viewModel = new ViewModelProvider(this).get(StockViewModel.class);
        viewModel.error().observe(this, this::showError);
        viewModel.venteCreated().observe(this, unused -> this.showSuccess(R.string.vente_created_message));
        viewModel.venteUpdated().observe(this, unused -> this.showSuccess(R.string.vente_updated_message));
        viewModel.fetch();
    }

    private void showSuccess(@StringRes int message, Object... formatArgs) {
        Snackbar.make(
                binding.getRoot(),
                getString(message, formatArgs),
                BaseTransientBottomBar.LENGTH_SHORT
        ).show();
    }

    private void showError(@NotNull io.github.rusted.simplestock.util.Error error) {
        Snackbar.make(
                binding.getRoot(),
                getString(R.string.error_formatted, error.message, error.exception.getMessage()),
                BaseTransientBottomBar.LENGTH_SHORT
        ).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
               || super.onSupportNavigateUp();
    }
}