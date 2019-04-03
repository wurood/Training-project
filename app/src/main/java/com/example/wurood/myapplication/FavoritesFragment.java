package com.example.wurood.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FavoritesFragment extends Fragment {
    private ContactAdapter mAdapter;
    private CallbackFavorites mCallBackFavorites;
    DBHelper mDBHelper;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;


    public FavoritesFragment() {
    }

    public void setDatabase(DBHelper d) {
        mDBHelper = d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDBHelper = new DBHelper(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ContactAdapter(getActivity(), new ArrayList<Contact>(), new ArrayList<Contact>());
        mAdapter.setFavoritesListener(mCallBackFavorites);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                Contact obj = mAdapter.getList().get(position);
                ((MainActivity) getActivity()).onCardDeletes(obj, position);
                mAdapter.notifyDataSetChanged();


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void addToFavorites(List<Contact> favoriteList) {
        mAdapter.setItemList(favoriteList);
        mAdapter.notifyDataSetChanged();
    }

    public void notifyItem() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallbackFavorites) {
            mCallBackFavorites = (CallbackFavorites) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FavoritesFragment.CallbackFavorites");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBackFavorites = null;
    }


    public interface CallbackFavorites {
        // TODO: Update argument type and name
        void onCardDeletes(Contact Contact, int position);
    }
}
