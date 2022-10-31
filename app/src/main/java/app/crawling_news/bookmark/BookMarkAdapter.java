package app.crawling_news.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.crawling_news.R;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ViewHolder> {
    private ArrayList<BookMarkItem> mData;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    BookMarkAdapter(ArrayList<BookMarkItem> list) {
        mData = list;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @NonNull
    @Override
    public BookMarkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.book_list_item, parent, false);

        return new BookMarkAdapter.ViewHolder(view);
    }

    private BookMarkAdapter.OnItemClickListener mListener = null;
    private BookMarkAdapter.OnItemLongClickListener longClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(BookMarkAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(BookMarkAdapter.OnItemLongClickListener listener){
        this.longClickListener = listener;
    }

    // onBindViewHolder() - position 에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(BookMarkAdapter.ViewHolder holder, int position) {

        BookMarkItem item = mData.get(position);

        holder.text.setText(item.getText());
        holder.img.setImageDrawable(item.getImg());
        holder.time.setText(item.getTime());

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text, time;

        ViewHolder(final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        if (longClickListener != null) {
                            longClickListener.onItemLongClick(v, position);
                        }
                    }
                    return false;
                }
            });

            // 뷰 객체에 대한 참조. (hold strong reference)
            img = itemView.findViewById(R.id.bookImg);
            text = itemView.findViewById(R.id.bookText);
            time = itemView.findViewById(R.id.bookTime);

        }
    }
}
