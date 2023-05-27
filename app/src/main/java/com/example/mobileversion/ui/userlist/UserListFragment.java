package com.example.mobileversion.ui.userlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mobileversion.R;
import com.example.mobileversion.databinding.FragmentSettingsBinding;
import com.example.mobileversion.databinding.FragmentUserlistBinding;
import com.example.mobileversion.ui.piece.PiecesFragment;
import com.example.mobileversion.ui.piece.anime.AnimeFragment;
import com.example.mobileversion.ui.piece.manga.MangaFragment;
import com.example.mobileversion.ui.piece.ranobe.RanobeFragment;
import com.example.mobileversion.ui.userlist.anime.UserAnimeFragment;
import com.example.mobileversion.ui.userlist.manga.UserMangaFragment;
import com.example.mobileversion.ui.userlist.ranobe.UserRanobeFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {
    private FragmentUserlistBinding binding;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserlistBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewPager = root.findViewById(R.id.view_pager);
        tabLayout = root.findViewById(R.id.tab_layout);

        setupViewPager();
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setupViewPager() {
        UserListFragment.ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new UserAnimeFragment(), "Anime");
        adapter.addFragment(new UserMangaFragment(), "Manga");
        adapter.addFragment(new UserRanobeFragment(), "Ranobe");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}
