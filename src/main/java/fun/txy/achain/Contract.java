package fun.txy.achain;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contract {

  public enum Function {
    TRANSFER_TO("transfer_to");

    public static Function fromString(String name) {
      return Function.valueOf(name.toUpperCase());
    }


    private final String name;

    Function(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public List<Event> getSupportEvent() {
      if (this == Function.TRANSFER_TO) {
        return Arrays.asList(Event.TRANSFER_TO_SUCCESS, Event.TRANSFER_TO_FAIL);
      }
      return new ArrayList<>();
    }

  }

  public enum Event {
    TRANSFER_TO_SUCCESS("transfer_to_success", true),
    TRANSFER_TO_FAIL("transfer_to_fail", true);

    public static Function fromString(String name) {
      return Function.valueOf(name.toUpperCase());
    }

    private final String name;
    private final boolean notNull;

    Event(String name, boolean notNull) {
      this.name = name;
      this.notNull = notNull;
    }

    public String getName() {
      return name;
    }

    public boolean isNotNull() {
      return notNull;
    }
  }

  public static Common create(String contractId) {
    return new Common(contractId, null);
  }

  public static class Common extends Body {
    private final String contractId;
    private final String coinName;

    public Common(String contractId, String coinName) {
      this.contractId = contractId;
      this.coinName = coinName;
    }

    @Override
    public Call call(String method, String args) {
      return super.call(method, args);
    }

    @Override
    String id() {
      return contractId;
    }

    @Override
    String name() {
      return coinName;
    }
  }

  public static class Call {
    private ACTAddress contractAddress;
    private String method;
    private String args;


    Call(ACTAddress contractAddress, String method, String args) {
      this.contractAddress = contractAddress;
      this.method = method;
      this.args = args;
    }

    public String getMethod() {
      return method;
    }

    public String getArgs() {
      return args;
    }

    public ACTAddress getContractAddress() {
      return contractAddress;
    }

    @Override
    public String toString() {
      return "address:" + contractAddress.getAddressStr() + "|method:" + method + "|args:" + args;
    }
  }

  public static class TransferToCall extends Call {
    private long amount;
    private String toAddress;

    TransferToCall(
        ACTAddress contractAddress,
        String method,
        String args,
        String toAddress,
        long amount) {
      super(contractAddress, method, args);
      this.toAddress = toAddress;
      this.amount = amount;
    }

    public String getToAddress() {
      return toAddress;
    }

    public long getAmount() {
      return amount;
    }
  }

  private static abstract class Body {
    private static final int _scale = 6;
    private static final BigDecimal _2bd = new BigDecimal("10").pow(_scale - 1);
    private ACTAddress actAddress;

    public ACTAddress getActAddress() {
      if (actAddress == null) {
        actAddress = new ACTAddress(id(), ACTAddress.Type.CONTRACT);
      }
      return actAddress;
    }

    String makeTransferArgs(String toAddress, long amount) {
      return toAddress + "|" + new BigDecimal(amount).divide(_2bd, _scale, BigDecimal.ROUND_DOWN)
          .stripTrailingZeros().toPlainString();
    }

    public TransferToCall transferTo(String toAddress, long amount) {
      return new TransferToCall(
          getActAddress(),
          Function.TRANSFER_TO.getName(),
          makeTransferArgs(toAddress, amount),
          toAddress,
          amount);
    }

    Call call(String method, String args) {
      return new Call(getActAddress(), method, args);
    }

    abstract String id();

    abstract String name();
  }
}
