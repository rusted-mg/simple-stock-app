package io.github.rusted.simplestock;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import io.github.rusted.simplestock.data.ConnectionProvider;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.data.VenteRepository;
import io.github.rusted.simplestock.databinding.FragmentSalesChartBinding;
import io.github.rusted.simplestock.util.AndroidConfig;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalesChartFragment extends Fragment {

    private FragmentSalesChartBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSalesChartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupChart();
        loadChartData();
    }

    private void setupChart() {
        // Get theme colors
        int textColor = getResources().getColor(R.color.chart_text_color, requireContext().getTheme());
        int backgroundColor = getResources().getColor(R.color.chart_background_color, requireContext().getTheme());

        // Configure chart appearance for a bar chart
        binding.salesChart.setDrawBarShadow(false);
        binding.salesChart.setDrawValueAboveBar(true);
        binding.salesChart.getDescription().setEnabled(false);
        binding.salesChart.setPinchZoom(true);
        binding.salesChart.setDrawGridBackground(false);
        binding.salesChart.setExtraOffsets(10f, 10f, 10f, 20f);

        // Set legend properties
        Legend legend = binding.salesChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextColor(textColor);
        legend.setTextSize(12f);

        // Configure axes
        XAxis xAxis = binding.salesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = binding.salesChart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = binding.salesChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Additional chart configuration
        binding.salesChart.setHighlightPerTapEnabled(true);
        binding.salesChart.animateY(1400);
        binding.salesChart.setNoDataText(getString(R.string.sales_distribution));
        binding.salesChart.setBackgroundColor(backgroundColor);
    }

    private void loadChartData() {
        new Thread(() -> {
            try {
                VenteRepository repository = new VenteRepository(new ConnectionProvider(new AndroidConfig(requireContext())));
                List<Vente> ventes = repository.all();

                if (!ventes.isEmpty()) {
                    requireActivity().runOnUiThread(() -> createHistogram(ventes));
                }
            } catch (SQLException e) {
                Log.e("SimpleStock", "loadChartData: Unable to load", e);
            }
        }).start();
    }

    private void createHistogram(@NotNull List<Vente> ventes) {
        List<Map.Entry<Vente, Double>> ventesWithRevenue = ventes.stream()
                .map(vente -> Map.entry(vente, vente.montant()))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        if (ventesWithRevenue.isEmpty()) return;

        // Take top 5 sales or all if less than 5
        int displayCount = Math.min(ventesWithRevenue.size(), 5);
        List<Map.Entry<Vente, Double>> topSales = ventesWithRevenue.subList(0, displayCount);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // Add each item to the chart
        for (int i = 0; i < topSales.size(); i++) {
            Map.Entry<Vente, Double> sale = topSales.get(i);
            // Add the value as a bar entry
            entries.add(new BarEntry(i, sale.getValue().floatValue()));
            // Add truncated product name as label
            labels.add(truncateProductName(sale.getKey().getDesign(), 10));
        }

        // Get theme colors
        int textColor = getResources().getColor(R.color.chart_text_color, requireContext().getTheme());

        // Create dataset and set appearance
        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.sales_amount));
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(textColor);

        // Create bar data and set to chart
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        // Configure X axis with labels
        XAxis xAxis = binding.salesChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(textColor);
        xAxis.setDrawGridLines(false);

        // Configure Y axis
        YAxis leftAxis = binding.salesChart.getAxisLeft();
        leftAxis.setTextColor(textColor);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = binding.salesChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Set data to chart and refresh
        binding.salesChart.setData(barData);
        binding.salesChart.setFitBars(true);
        binding.salesChart.invalidate();
    }

    /**
     * Truncates a product name to a specified length and adds ellipsis if needed
     * @param name Product name to truncate
     * @param maxLength Maximum length for the name
     * @return Truncated product name
     */
    private String truncateProductName(String name, int maxLength) {
        if (name == null || name.length() <= maxLength) {
            return name;
        }
        return name.substring(0, maxLength - 3) + "...";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
