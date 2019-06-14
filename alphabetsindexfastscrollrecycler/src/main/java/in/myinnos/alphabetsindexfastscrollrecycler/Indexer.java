package in.myinnos.alphabetsindexfastscrollrecycler;

import android.widget.SectionIndexer;

import java.util.ArrayList;

/**
 * @author jx on 2019/6/14.
 */
public interface Indexer extends SectionIndexer {
    ArrayList<Integer> getIndex();
}
