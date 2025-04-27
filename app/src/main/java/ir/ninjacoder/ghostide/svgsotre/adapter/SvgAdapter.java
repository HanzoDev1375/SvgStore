package ir.ninjacoder.ghostide.svgsotre.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import ir.ninjacoder.ghostide.svgsotre.model.SvgItem;
import ir.ninjacoder.ghostide.svgsotre.R;
import android.graphics.drawable.PictureDrawable;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class SvgAdapter extends RecyclerView.Adapter<SvgAdapter.SvgViewHolder> {
    private List<SvgItem> svgItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SvgItem item);
        void onDownloadClick(SvgItem item);
    }

    public SvgAdapter(List<SvgItem> svgItems, OnItemClickListener listener) {
        this.svgItems = svgItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SvgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_extension, parent, false);
        return new SvgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SvgViewHolder holder, int position) {
        SvgItem item = svgItems.get(position);
        
        holder.svgTitle.setText(item.getName());
        
        try {
            SVG svg = SVG.getFromString(item.getSvgContent());
            holder.svgImageView.setImageDrawable(new PictureDrawable(svg.renderToPicture()));
        } catch (SVGParseException e) {
            holder.svgImageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });

        holder.downloadButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDownloadClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return svgItems.size();
    }

    public void updateData(List<SvgItem> newItems) {
        svgItems = newItems;
        notifyDataSetChanged();
    }

    static class SvgViewHolder extends RecyclerView.ViewHolder {
        ImageView svgImageView;
        TextView svgTitle;
        MaterialButton downloadButton;

        public SvgViewHolder(@NonNull View itemView) {
            super(itemView);
            svgImageView = itemView.findViewById(R.id.svgImageView);
            svgTitle = itemView.findViewById(R.id.svgTitle);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }
    }
}