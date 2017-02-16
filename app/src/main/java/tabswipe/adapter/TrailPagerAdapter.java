package tabswipe.adapter;

import com.dylan.blast.Blast_Radius;
import com.dylan.blast.ListPair;
import com.dylan.blast.Past_Blasts;
import com.dylan.blast.TrailersFragment;
import com.dylan.blast.Trailing;
import com.dylan.blast.TrailingFragment;
import com.dylan.blast.Trending;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Dylan on 1/7/2017.
 */

public class TrailPagerAdapter extends  FragmentPagerAdapter{

    ListPair pair = null;

    public TrailPagerAdapter(FragmentManager fm, ListPair pair) {
        super(fm);
        this.pair = pair;
    }


    @Override
    public android.support.v4.app.Fragment getItem(int index) {

        switch (index) {
            case 0:

                return new TrailersFragment().setPair(pair);
            case 1:

                return new TrailingFragment().setPair(pair);
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}

