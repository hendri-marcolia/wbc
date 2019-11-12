package jp.co.soramitsu.iroha.android.sample.view.main.history;

import androidx.lifecycle.ViewModelProviders;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.orm.SugarRecord;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import jp.co.soramitsu.iroha.android.sample.data.Transaction;
import jp.co.soramitsu.iroha.android.sample.entity.TransactionEntity;
import jp.co.soramitsu.iroha.android.sample.interactor.GetAccountTransactionsInteractor;
import lombok.Setter;

public class HistoryPresenter {

    @Setter
    private HistoryFragment fragment;

    private final GetAccountTransactionsInteractor getAccountTransactionsInteractor;

    private TransactionsViewModel transactionsViewModel;

    @Inject
    public HistoryPresenter(GetAccountTransactionsInteractor getAccountTransactionsInteractor) {
        this.getAccountTransactionsInteractor = getAccountTransactionsInteractor;
    }

    void onCreateView() {
        transactionsViewModel = ViewModelProviders.of(fragment).get(TransactionsViewModel.class);
    }

    void getTransactions() {
//        getAccountTransactionsInteractor.execute(
//                transactions -> {
//                    Collections.sort(transactions, (o1, o2) -> o2.date.compareTo(o1.date));
//                    transactionsViewModel.getTransactions().postValue();
//                    fragment.finishRefresh();
//                },
//                throwable -> fragment.didError(throwable));
        List<TransactionEntity> entities =  SugarRecord.listAll(TransactionEntity.class);
        transactionsViewModel.getTransactions().postValue(transformTransactions(entities));
        fragment.finishRefresh();
    }

    private List transformTransactions(List<TransactionEntity> transactions) {
        if (transactions.isEmpty()) {
            return Collections.emptyList();
        }
        List listItems = new ArrayList();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();

        c.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = c.getTime();

        c = Calendar.getInstance();
        c.set(Calendar.HOUR, -1);
        Date oneHourBefore = c.getTime();

        SimpleDateFormat headerDateFormat = new SimpleDateFormat("MMMM, dd", Locale.getDefault());
        SimpleDateFormat hoursDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String currentPrettyDate = getHeader(new Date(new Gson().fromJson(transactions.get(0).getTransactionPayload(),
                jp.co.soramitsu.iroha.android.sample.data.Transaction.class).getTransactionPayload()
                        .getPayload().getTimestamp()), headerDateFormat,
                today, yesterday);

        listItems.add(currentPrettyDate);
        Gson gson = new Gson();
        for (TransactionEntity transactionEntity : transactions) {
            Transaction transaction = gson.fromJson(transactionEntity.getTransactionPayload(), Transaction.class);
            Date txDate = new Date(transaction.getTransactionPayload().getPayload().getTimestamp());
            if (!getHeader(txDate, headerDateFormat, today, yesterday)
                    .equals(currentPrettyDate)) {
                currentPrettyDate = getHeader(txDate, headerDateFormat,
                        today, yesterday);
                listItems.add(currentPrettyDate);
            }

            BigDecimal amount = new BigDecimal(transaction.getTransactionPayload().getPayload().getAmount());
            String prettyAmount = amount.toString();

            String prettyDate;
            if (currentPrettyDate.equals("Today") && txDate.after(oneHourBefore)) {
                long duration = new Date().getTime() - txDate.getTime();
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                if (diffInMinutes == 0) {
                    prettyDate = "just now";
                } else {
                    prettyDate = diffInMinutes + " minutes ago";
                }
            } else {
                prettyDate = hoursDateFormat.format(txDate);
            }


            TransactionVM vm = new TransactionVM(transactionEntity.getId(), prettyDate, transaction.getAgentId(), prettyAmount, transaction.getTransactionType().name(), transactionEntity.isCommited() ? "SYNCED" : "Waiting");

            listItems.add(vm);
        }
        return listItems;
    }

    private String getHeader(Date date, SimpleDateFormat dateFormat, Date today, Date yesterday) {
        if (DateUtils.isToday(date.getTime())) {
            return "Today";
        } else if (date.before(today) && date.after(yesterday)) {
            return "Yesterday";
        } else {
            return dateFormat.format(date);
        }
    }

    void onStop() {
        fragment = null;
        getAccountTransactionsInteractor.unsubscribe();
    }

    public void successSync(){
        fragment.successSync();
    }

    public void showInfo(String msg) {
        fragment.showInfo(msg);
    }

    public void showError(Throwable throwable){
        fragment.showError(throwable);
    }
}