package io.github.rusted.simplestock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.rusted.simplestock.adapter.StockAdapter;
import io.github.rusted.simplestock.data.Vente;
import io.github.rusted.simplestock.databinding.FragmentStockListBinding;
import io.github.rusted.simplestock.enums.StockFormOperationMode;
import io.github.rusted.simplestock.viewmodel.StockViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StockListFragment extends Fragment {

    private FragmentStockListBinding binding;
    private boolean isStatsVisible = false;
    private List<Vente> currentVentes;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentStockListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupFabButtons();
        setupSwipeToEdit();
        setupMenuProvider();
    }

    private void setupRecyclerView() {
        StockAdapter stockAdapter = new StockAdapter();
        StockViewModel viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        binding.recyclerviewStock.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerviewStock.setAdapter(stockAdapter);
        viewModel.ventes().observe(getViewLifecycleOwner(), ventes -> {
            stockAdapter.submitList(ventes);
            currentVentes = ventes;
            updateStatsView(ventes);
        });
    }

    private void setupFabButtons() {
        // Setup Toggle Stats FAB
        binding.fabToggleStats.setOnClickListener(v -> toggleStatsVisibility());
    }

    private void toggleStatsVisibility() {
        isStatsVisible = !isStatsVisible;

        // Update the FAB icon and tint based on state
        if (isStatsVisible) {
            binding.fabToggleStats.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            binding.fabToggleStats.setContentDescription(getString(R.string.hide_stats));
            binding.fabToggleStats.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.fab_active)));

            // Show stats card with animation
            binding.cardStats.setVisibility(View.VISIBLE);
            binding.cardStats.setAlpha(0f);
            binding.cardStats.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setListener(null);

            // Ensure stats are up to date
            if (currentVentes != null) {
                updateStatsView(currentVentes);
            }
        } else {
            binding.fabToggleStats.setImageResource(android.R.drawable.ic_menu_info_details);
            binding.fabToggleStats.setContentDescription(getString(R.string.show_stats));
            binding.fabToggleStats.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.fab_inactive)));

            // Hide stats card with animation
            binding.cardStats.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            binding.cardStats.setVisibility(View.GONE);
                        }
                    });
        }

        // Update RecyclerView constraints based on stats visibility
        updateRecyclerViewConstraints();
    }

    private void updateRecyclerViewConstraints() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) binding.recyclerviewStock.getParent());

        if (isStatsVisible) {
            constraintSet.connect(
                    R.id.recyclerview_stock,
                    ConstraintSet.BOTTOM,
                    R.id.card_stats,
                    ConstraintSet.TOP,
                    (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            12,
                            getResources().getDisplayMetrics()
                    )
            );
        } else {
            constraintSet.connect(
                    R.id.recyclerview_stock,
                    ConstraintSet.BOTTOM,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.BOTTOM,
                    0
            );
        }

        TransitionManager.beginDelayedTransition((ViewGroup) binding.recyclerviewStock.getParent());
        constraintSet.applyTo((ConstraintLayout) binding.recyclerviewStock.getParent());
    }

    private void setupMenuProvider() {
        // Setup menu provider
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.list_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_add) {
                    var action = StockListFragmentDirections.actionStockListToStockForm();
                    action.setOperation(StockFormOperationMode.CREATE);
                    NavHostFragment.findNavController(StockListFragment.this).navigate(action);
                    return true;
                } else if (menuItem.getItemId() == R.id.action_view_chart) {
                    NavHostFragment.findNavController(StockListFragment.this)
                            .navigate(StockListFragmentDirections.actionStockListToSalesChart());
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void updateStatsView(List<Vente> ventes) {
        if (ventes == null || ventes.isEmpty()) {
            binding.statsMaxMontant.setText(getString(R.string.stats_max_montant, "0.00"));
            binding.statsMinMontant.setText(getString(R.string.stats_min_montant, "0.00"));
            binding.statsTotalMontant.setText(getString(R.string.stats_total_montant, "0.00"));
            return;
        }

        double maxMontant = Double.MIN_VALUE;
        double minMontant = Double.MAX_VALUE;
        double totalMontant = 0.0;

        for (Vente vente : ventes) {
            double montant = vente.montant();
            maxMontant = Math.max(maxMontant, montant);
            minMontant = Math.min(minMontant, montant);
            totalMontant += montant;
        }

        // Format the values with two decimal places
        NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        formatter.setGroupingUsed(true); // Use thousand separators

        binding.statsMaxMontant.setText(getString(R.string.stats_max_montant, formatter.format(maxMontant)));
        binding.statsMinMontant.setText(getString(R.string.stats_min_montant, formatter.format(minMontant)));
        binding.statsTotalMontant.setText(getString(R.string.stats_total_montant, formatter.format(totalMontant)));
    }

    private void setupSwipeToEdit() {
        new ItemTouchHelper(new StockItemSwipeCallback()).attachToRecyclerView(binding.recyclerviewStock);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class StockItemSwipeCallback extends ItemTouchHelper.SimpleCallback {
        private final Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.swipe_edit_background);
        private final Drawable editIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_edit);
        private final Paint textPaint = new Paint();

        public StockItemSwipeCallback() {
            super(0, ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Get the swiped item
            int position = viewHolder.getAdapterPosition();
            StockAdapter adapter = (StockAdapter) binding.recyclerviewStock.getAdapter();
            if (adapter != null) {
                var action = StockListFragmentDirections.actionStockListToStockForm();
                action.setOperation(StockFormOperationMode.UPDATE);
                Vente vente = adapter.getCurrentList().get(position);
                action.setVente(vente);
                action.setVenteId(vente.getNumProduit());
                NavHostFragment.findNavController(StockListFragment.this).navigate(action);

                // Reset the swipe (important to avoid item disappearing from the list)
                adapter.notifyItemChanged(position);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // Add visual cues during swipe
            View itemView = viewHolder.itemView;
            // Don't draw if item is swiped out of view
            if (dX == 0) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }
            // Draw the blue background
            assert background != null;
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
            background.draw(c);

            // Calculate position for the edit icon
            assert editIcon != null;
            int iconTop = itemView.getTop() + (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + editIcon.getIntrinsicHeight();
            int iconMargin = 16;
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = itemView.getLeft() + iconMargin + editIcon.getIntrinsicWidth();

            // Draw the edit icon
            editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            editIcon.draw(c);

            // Setup text paint for "Edit" text
            textPaint.setColor(resolveColor(com.google.android.material.R.attr.colorOnSecondaryContainer));
            textPaint.setTextSize(36);
            textPaint.setAntiAlias(true);

            // Draw "Edit" text
            float textX = iconRight + iconMargin;
            float textY = itemView.getTop() + (itemView.getHeight() + textPaint.getTextSize()) / 2;
            c.drawText(getString(R.string.swipe_edit_label), textX, textY, textPaint);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        private int resolveColor(int colorOnSecondaryContainer) {
            TypedValue typedValue = new TypedValue();
            Context context = requireContext();
            context.getTheme().resolveAttribute(colorOnSecondaryContainer, typedValue, true);
            return ContextCompat.getColor(context, typedValue.resourceId);
        }
    }
}
