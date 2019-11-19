package jp.co.soramitsu.iroha.android.sample.adapter;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import jp.co.soramitsu.iroha.android.sample.view.deposit.DepositFragment;
import jp.co.soramitsu.iroha.android.sample.view.main.history.HistoryFragment;
import jp.co.soramitsu.iroha.android.sample.view.withdraw.WithdrawFragment;


public class MainAdapter extends FragmentPagerAdapter {
    Map<Integer, Fragment> fragmentMap = new HashMap<>();
    public MainAdapter(FragmentManager manager) {
        super(manager);
        fragmentMap.put(0, new DepositFragment());
        fragmentMap.put(1, new WithdrawFragment());
        fragmentMap.put(2, new HistoryFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentMap.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "DEPOSIT";
        } else if (position == 1) {
            return "WITHDRAW";
        } else {
            return "HISTORY";
        }
    }
}
