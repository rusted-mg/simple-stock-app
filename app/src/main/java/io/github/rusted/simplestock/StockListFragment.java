package io.github.rusted.simplestock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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

public class StockListFragment extends Fragment {

    private FragmentStockListBinding binding;

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
        binding.fabAddProduct.setOnClickListener(v -> {
            var action = StockListFragmentDirections.actionStockListToStockForm();
            action.setOperation(StockFormOperationMode.CREATE);
            NavHostFragment.findNavController(this).navigate(action);
        });
        StockAdapter stockAdapter = new StockAdapter();
        StockViewModel viewModel = new ViewModelProvider(requireActivity()).get(StockViewModel.class);
        binding.recyclerviewStock.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.recyclerviewStock.setAdapter(stockAdapter);
        viewModel.ventes().observe(getViewLifecycleOwner(), stockAdapter::submitList);
        setupSwipeToEdit();
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
