package net.schwitzkroko.demo.validplate.plate;

public sealed interface PlateModel {

    /** Failed parse — carries no data. */
    record Invalid() implements PlateModel {
        public static final Invalid INSTANCE = new Invalid();
    }

    /** Successful parse — the DTO. */
    record Valid(
            String districtCode,
            String letters,
            String digits
    ) implements PlateModel {

        /** Normalised canonical form, e.g. "LI-IT100". */
        public String formatted() {
            return districtCode + "-" + letters + digits;
        }
    }
}