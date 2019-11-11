package jp.co.soramitsu.iroha.android.sample.interactor;


import com.google.protobuf.ByteString;

import java.util.Iterator;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import iroha.protocol.Endpoint;
import iroha.protocol.Primitive;
import jp.co.soramitsu.iroha.android.sample.R;
import jp.co.soramitsu.iroha.android.sample.SampleApplication;
import jp.co.soramitsu.iroha.java.IrohaAPI;

class Interactor {

    final CompositeDisposable subscriptions = new CompositeDisposable();
    final Scheduler jobScheduler;
    final Scheduler uiScheduler;

    Interactor(Scheduler jobScheduler, Scheduler uiScheduler) {
        this.jobScheduler = jobScheduler;
        this.uiScheduler = uiScheduler;
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    protected IrohaAPI getIrohaAPI(){
        return new IrohaAPI(SampleApplication.instance.getApplicationContext().getString(R.string.iroha_url),
                SampleApplication.instance.getApplicationContext().getResources().getInteger(R.integer.iroha_port));
    }

//    static byte[] toByteArray(ByteVector blob) {
//        byte bs[] = new byte[(int) blob.size()];
//        for (int i = 0; i < blob.size(); ++i) {
//            bs[i] = (byte) blob.get(i);
//        }
//        return bs;
//    }
//
//    static boolean isTransactionSuccessful(CommandServiceGrpc.CommandServiceBlockingStub stub, UnsignedTx utx) {
//        ByteVector txhash = utx.hash().blob();
//        byte bshash[] = toByteArray(txhash);
//
//        EndPoint.TxStatusRequest request = EndPoint.TxStatusRequest.newBuilder().setTxHash(ByteString.copyFrom(bshash)).build();
//
//        Iterator<EndPoint.ToriiResponse> features = stub.statusStream(request);
//
//        EndPoint.ToriiResponse response = null;
//        while (features.hasNext()) {
//            response = features.next();
//        }
//        return response.getTxStatus() == EndPoint.TxStatus.COMMITTED;
//    }


}