package jp.co.soramitsu.iroha.android.sample.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import jp.co.soramitsu.iroha.android.sample.view.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.view.main.receive.ReceiveFragment;
import jp.co.soramitsu.iroha.android.sample.view.main.send.SendFragment;
import jp.co.soramitsu.iroha.android.sample.view.pendingtransaction.PendingTransactionFragment;
import jp.co.soramitsu.iroha.android.sample.view.scan.ScanFragment;


public class MainAdapter extends FragmentPagerAdapter {

    public MainAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ScanFragment();
        } else if (position == 1) {
            return new PendingTransactionFragment();
        } else {
            return new HistoryFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "SCAN";
        } else if (position == 1) {
            return "PENDING TRANSACTION";
        } else {
            return "HISTORY";
        }
    }
}
