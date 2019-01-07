package example.domain.model.payroll;

import example.domain.model.attendance.Attendance;
import example.domain.model.contract.Contract;
import example.domain.model.contract.WorkerContract;
import example.domain.model.timerecord.TimeRecord;
import example.domain.model.worker.Name;
import example.domain.model.worker.WorkerNumber;

import java.math.BigDecimal;

/**
 * 給与
 */
public class Payroll {

    WorkerContract workerContract;
    Attendance attendance;

    public Payroll(WorkerContract workerContract, Attendance attendance) {
        this.workerContract = workerContract;
        this.attendance = attendance;
    }

    public WorkerNumber workerNumber() {
        return workerContract.workerNumber();
    }

    public Name workerName() {
        return workerContract.workerName();
    }

    public PaymentAmount totalPaymentAmount() {
        PaymentAmount paymentAmount = new PaymentAmount(BigDecimal.ZERO);

        for (TimeRecord timeRecord : attendance.listAvailableWorkRecord()) {
            Contract contract = workerContract.availableContractAt(timeRecord.workDate().value());

            PaymentAmount oneDayAmount = new PaymentAmount(timeRecord.actualWorkTime(), contract.wageCondition());
            paymentAmount = paymentAmount.add(oneDayAmount);
        }
        return paymentAmount;
    }

    public PayrollStatus payrollStatus() {
        if (attendance.notWorking()) {
            return PayrollStatus.稼働登録無し;
        }
        return PayrollStatus.from(workerContract.contractStatus(attendance.firstWorkDate().value()));
    }
}
