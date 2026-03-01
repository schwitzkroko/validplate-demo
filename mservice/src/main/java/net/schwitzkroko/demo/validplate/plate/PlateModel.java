package net.schwitzkroko.demo.validplate.plate;

public sealed interface PlateModel {

  /** Normalised canonical form, e.g. "LI-IT100". "error for invalid plates. */
  String canonical();

  /** Failed parse — carries no data. */
  record Invalid() implements PlateModel {
    public static final Invalid INSTANCE = new Invalid();

    @Override
    public String canonical() {
      return "error";
    }
  }

  /** Successful parse — the DTO. */
  record Valid(String districtCode, String letters, String digits) implements PlateModel {

    @Override
    public String canonical() {
      return districtCode + "-" + letters + digits;
    }
  }
}
