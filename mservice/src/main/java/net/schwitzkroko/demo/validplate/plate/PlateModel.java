package net.schwitzkroko.demo.validplate.plate;

public sealed interface PlateModel {

  /**
   * Normalised canonical form, e.g. "LI-IT100". String "error" for invalid
   * plates.
   */
  String canonical();

  /**
   * Input was parsed but is invalid — e.g. contains a non-existent district code
   * (e.g. "ZZ-IT100") or has an invalid letter/digit structure (e.g. "LI-1T100").
   */
  record Invalid() implements PlateModel {
    public static final Invalid INSTANCE = new Invalid();

    @Override
    public String canonical() {
      return "error";
    }
  }

  /**
   * Input could not be parsed at all — contains illegal characters or structure
   * that prevents even pattern matching (e.g. "HalloWelt!").
   */
  record Unparsable() implements PlateModel {
    public static final Unparsable INSTANCE = new Unparsable();

    @Override
    public String canonical() {
      return "error";
    }
  }

  /**
   * Ambiguous parse — no separator was present so the letter/digit boundary is
   * unclear (e.g. "LIIT100"). The whole letter prefix is in {@code districtCode};
   * {@code letters} is always empty.
   */
  record Ambiguous(String distinctCode, String digits) implements PlateModel {

    @Override
    public String canonical() {
      return distinctCode + "-" + digits;
    }
  }

  /** Unambiguous successful parse — separator was present. */
  record Valid(String distinctCode, String letters, String digits) implements PlateModel {

    @Override
    public String canonical() {
      return distinctCode + "-" + letters + digits;
    }
  }
}
