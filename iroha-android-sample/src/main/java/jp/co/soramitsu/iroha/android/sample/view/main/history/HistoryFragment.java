package jp.co.soramitsu.iroha.android.sample.view.main.history;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.android.sample.databinding.FragmentHistoryBinding;
import jp.co.soramitsu.iroha.android.sample.interactor.basicsavingaction.PerformSaveOfflineInteractor;
import jp.co.soramitsu.iroha.android.sample.view.main.MainActivity;

public class HistoryFragment extends Fragment implements HistoryView {
    private FragmentHistoryBinding binding;

    @Inject
    HistoryPresenter presenter;

    @Inject
    PerformSaveOfflineInteractor performSaveOfflineInteractor;

    private TransactionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        SampleApplication.instance.getApplicationComponent().inject(this);

        presenter.setFragment(this);
        presenter.onCreateView();
        presenter.getTransactions();

        TransactionsViewModel transactionsViewModel = ViewModelProviders.of(this).get(TransactionsViewModel.class);
        transactionsViewModel.getTransactions().observe(this, transactions -> {
            DiffUtil.Callback transactionDiffChecker =
                    new TransactionDiffChecker(adapter.getTransactions(), transactions);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(transactionDiffChecker);
            adapter.setTransactions(transactions);
            diffResult.dispatchUpdatesTo(adapter);
        });

        configureRecycler();
        binding.refresh.setOnRefreshListener(() -> presenter.getTransactions());
        return binding.getRoot();
    }

    private void configureRecycler() {
        binding.transactions.setHasFixedSize(true);
        binding.transactions.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionsAdapter(performSaveOfflineInteractor, presenter);
        binding.transactions.setAdapter(adapter);
    }

    public void successSync(String msg){
        ((MainActivity)getActivity()).refreshData(false);
        ((MainActivity)getActivity()).showInfo("Success sync Transaction, " +msg);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.setFragment(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void finishRefresh() {
        binding.refresh.setRefreshing(false);
    }

    @Override
    public void didError(Throwable error) {
        binding.refresh.setRefreshing(false);
        showError(error);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!getUserVisibleHint())return;
        presenter.getTransactions();
    }

    public void showInfo(String msg){
        ((MainActivity) getActivity()).showInfo(msg);
    }

    public void showError(Throwable error){
        ((MainActivity) getActivity()).showError(error);
    }



    public void showLoading() {
        ((MainActivity) getActivity()).showProgress();
    }


    public void hideLoading() {
        ((MainActivity) getActivity()).hideProgress();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && isResumed() && presenter != null)
            onResume();
    }
}