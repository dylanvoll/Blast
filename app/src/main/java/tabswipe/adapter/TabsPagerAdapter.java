package tabswipe.adapter;

import com.dylan.blast.Blast_Radius;
import com.dylan.blast.Past_Blasts;
import com.dylan.blast.Trailing;
import com.dylan.blast.Trending;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public android.support.v4.app.Fragment getItem(int index) {
 
        switch (index) {
        case 0:
           
            return new Blast_Radius();
        case 1:

            return new Trailing();
        case 2:
           
            return new Trending();
        case 3:
            
            return new Past_Blasts();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
 
}