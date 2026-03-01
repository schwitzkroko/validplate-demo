package net.schwitzkroko.demo.validplate.plate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.DistinctIdService;
import net.schwitzkroko.demo.validplate.distinct.district.DistrictRepo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


@ApplicationScoped
@Slf4j
class PlateServiceImpl implements PlateService {

    /**
     * Plate format after stripping whitespace:
     *   district code  : 1–3 uppercase letters   (e.g. LI, AA, B)
     *   optional hyphen: literal '-'
     *   letters part   : 1–3 uppercase letters   (e.g. IT, AB)
     *   digits part    : 1–4 digits               (e.g. 100, 1)
     *
     * Group 1 = district code, group 2 = letters, group 3 = digits
     */
    private static final Pattern PLATE_PATTERN =
            Pattern.compile("^([A-ZÄÖÜ]{1,3})-?([A-Z]{1,3})(\\d{1,4})$");

    @Inject
    DistinctIdService distinctIdService;

    @Override
    public PlateModel digest(String plate) {
        if (StringUtils.isBlank(plate)) {
            log.debug("digest: blank input");
            return PlateModel.Invalid.INSTANCE;
        }

        // Normalise: trim surrounding whitespace, collapse inner whitespace, uppercase
        String normalised = plate.strip().replaceAll("\\s+", "").toUpperCase();
        log.debug("digest: normalised='{}' from input='{}'", normalised, plate);

        Matcher m = PLATE_PATTERN.matcher(normalised);
        if (!m.matches()) {
            log.debug("digest: '{}' does not match plate pattern", normalised);
            return PlateModel.Invalid.INSTANCE;
        }

        String districtCode = m.group(1);
        String letters      = m.group(2);
        String digits       = m.group(3);

        if (distinctIdService.find(districtCode) == null) {
            log.debug("digest: district code '{}' not found in repo", districtCode);
            return PlateModel.Invalid.INSTANCE;
        }

        log.debug("digest: valid plate district='{}' letters='{}' digits='{}'",
                districtCode, letters, digits);
        return new PlateModel.Valid(districtCode, letters, digits);
    }
}
