package com.novatube.common.app.views;

import com.novatube.common.app.models.data.VideoGroup;
import com.novatube.common.app.models.search.MediaServiceSearchTagProvider;
import com.novatube.common.app.models.search.vineyard.Tag;

public interface SearchView {
    void updateSearch(VideoGroup group);
    void clearSearch();
    void clearSearchTags();
    void removeSearchTag(Tag tag);
    void setTagsProvider(MediaServiceSearchTagProvider provider);
    void showProgressBar(boolean show);
    void startSearch(String searchText);
    String getSearchText();
    void startVoiceRecognition();
    void finishReally();
}
