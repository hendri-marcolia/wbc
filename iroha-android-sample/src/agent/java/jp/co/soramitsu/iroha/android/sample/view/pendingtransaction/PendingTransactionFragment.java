package jp.co.soramitsu.iroha.android.sample.view.pendingtransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.adapter.PendingTransactionAdapter;
import jp.co.soramitsu.iroha.android.sample.data.PendingTransaction;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentPendingTransactionBinding;
import jp.co.soramitsu.iroha.android.sample.fragmentinterface.OnBackPressed;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.AgentPerformSaveOnlineInteractor;
import jp.co.soramitsu.iroha.android.sample.interactor.deposit.AgentSignPendingTransactionInteractor;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;

public class PendingTransactionFragment extends Fragment implements PendingTransactionView, OnBackPressed {
    private FragmentPendingTransactionBinding binding;

    private Handler rHandler = new Handler();

    @Inject
    PendingTransactionPresenter presenter;
    @Inject
    AgentPerformSaveOnlineInteractor agentPerformSaveOnlineInteractor;
    @Inject
    AgentSignPendingTransactionInteractor agentSignPendingTransactionInteractor;

    private RecyclerView recyclerView;
    private PendingTransactionAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pending_transaction, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);
        recyclerView = binding.pendingRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new PendingTransactionAdapter(presenter, agentPerformSaveOnlineInteractor, agentSignPendingTransactionInteractor);
        recyclerView.setAdapter(mAdapter);
        presenter.refreshPendingTransaction();
        binding.swipeContainer.setOnRefreshListener(() -> presenter.refreshPendingTransaction());
        return binding.getRoot();
    }

    @Override
    public void refreshTransaction(PendingTransaction pendingTransactions) {
        binding.swipeContainer.setRefreshing(false);
        mAdapter.addPendingTransaction(pendingTransactions);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearTransaction() {
        mAdapter.setPendingTransactions(new ArrayList<>());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }



    @Override
    public boolean onBackPressed() {
        return  true;
    }

    @Override
    public void showError(Throwable e) {
        binding.swipeContainer.setRefreshing(false);
        ((MainActivity) getActivity()).showError(e);
    }

    public void setRefreshing(boolean b){
        binding.swipeContainer.setRefreshing(b);
    }

    @Override
    public void showInfo(String msg) {
        binding.swipeContainer.setRefreshing(false);
        ((MainActivity) getActivity()).showInfo(msg);
    }

    @Override
    public void showLoading() {
        ((MainActivity) getActivity()).showProgress();
    }
}