package com.example.apionlyfans;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class PostDiffCallback extends DiffUtil.Callback {
    private List<PostAdapter.Post> oldList;
    private List<PostAdapter.Post> newList;

    public PostDiffCallback(List<PostAdapter.Post> oldList, List<PostAdapter.Post> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        PostAdapter.Post oldPost = oldList.get(oldItemPosition);
        PostAdapter.Post newPost = newList.get(newItemPosition);
        return oldPost.getId().equals(newPost.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        PostAdapter.Post oldPost = oldList.get(oldItemPosition);
        PostAdapter.Post newPost = newList.get(newItemPosition);
        return oldPost.getTitle().equals(newPost.getTitle())
                && oldPost.getDescription().equals(newPost.getDescription())
                && oldPost.getImageUrl().equals(newPost.getImageUrl());
    }
}
