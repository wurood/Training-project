package com.example.wurood.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<Contact> itemList, newArray, itemlistChiled;
    private HomeFragment.Callback mCallBack;
    private FavoritesFragment.CallbackFavorites mCallBackFavorite;


    public ContactAdapter(Context context, List<Contact> itemList, List<Contact> mitemlistChiled) {
        this.itemList = itemList;
        this.itemlistChiled = mitemlistChiled;
    }

    public void setHomeListener(HomeFragment.Callback callback) {
        this.mCallBack = callback;
    }

    public void setFavoritesListener(FavoritesFragment.CallbackFavorites callbackFavorites) {
        this.mCallBackFavorite = callbackFavorites;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ContactAdapter.ViewHolder holder, final int position) {
        final Contact contact = itemList.get(position);
        holder.ContactNameText.setText(contact.getName());
        holder.ContactNumberText.setText(contact.getContactNumber());
        holder.ContactPhoto.setImageBitmap(contact.getPhoto());
        holder.FavoritesPhoto.setImageResource(contact.getPhotoFav());
        holder.ContactEmailText.setText(contact.getEmail());
        final String contactName = contact.getName();
        final String contactDesc = contact.getContactNumber();
        final Bitmap contactPhoto = contact.getPhoto();
        final int FavoritePhoto = contact.getPhotoFav();
        final long contactId = contact.getid();
        final String contactEmail = contact.getEmail();

        try {

            if (contact.getPhoto() != null) {
                holder.ContactPhoto.setImageBitmap(contact.getPhoto());
            } else {
                Bitmap largeIcon = BitmapFactory.decodeResource(Resources.getSystem(), R.mipmap.ic_user);
                contact.setPhoto(largeIcon);
                holder.ContactPhoto.setImageResource(R.mipmap.ic_user);
            }

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onItemLongClick(holder.getAdapterPosition(), contact);
                }
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCallBack != null) {
                    mCallBack.onItemClick(holder.getAdapterPosition(), contact,holder.ContactPhoto);
                }

            }
        });
        holder.FavoritesPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View favoritesView) {

                BusStation.getBus().post(new Message(holder.getAdapterPosition(), contactName, contactDesc, contactPhoto, contactId, FavoritePhoto, contactEmail));

                contact.setPhotoFav(R.mipmap.ic_favorites);
                if (mCallBack != null) {
                    mCallBack.onCardSelected(holder.getAdapterPosition(), contactName, contactDesc, contactPhoto, contactId, FavoritePhoto, contactEmail);
                }
                if (mCallBackFavorite != null) {
                    mCallBackFavorite.onCardDeletes(contact, holder.getAdapterPosition());
                }
                notifyItemChanged(holder.getAdapterPosition());
            }
        });


        holder.ContactPhoto.setTag(holder);
        holder.FavoritesPhoto.setTag(holder);
    }

    public void setItemList(List<Contact> itemList) {
        this.itemList = itemList;
        newArray = itemList;
        for (int i = 0; i < itemList.size(); i++) {
            itemList.get(i).setPhotoFav(R.mipmap.ic_favorites);
        }
    }

    public List<Contact> getList() {
        return itemList;
    }
  public void  setl(List<Contact> itemList){
      this.itemList = itemList;

  }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_title)
        TextView ContactNameText;
        @BindView(R.id.list_desc)
        TextView ContactNumberText;
        @BindView(R.id.image_circular)
        ImageView ContactPhoto;
        @BindView(R.id.image_favorite)
        ImageView FavoritesPhoto;
        @BindView(R.id.list_email)
        TextView ContactEmailText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }


    }


}