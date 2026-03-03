package net.schwitzkroko.demo.validplate.plate;

import static net.schwitzkroko.demo.validplate.plate.PlateMatcherUtil.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.schwitzkroko.demo.validplate.distinct.DistinctId;
import net.schwitzkroko.demo.validplate.distinct.DistinctIdService;
import net.schwitzkroko.demo.validplate.plate.PlateModel.Ambiguous;

/**
 * Implementation of PlateService that parses and validates plate strings.
 * Reaches out to DistinctIdService to validate the "Unterscheidungszeichen"
 * code part of the plate.
 */
@ApplicationScoped
@Slf4j
class PlateServiceImpl implements PlateService {

  @Inject
  DistinctIdService distinctIdService;

  @Override
  public PlateModel digest(String plate) {

    if (plate == null || plate.isBlank()) {
      log.debug("digest: blank input");
      return PlateModel.Unparsable.INSTANCE;
    }

    String normalized = normalize(plate);

    PlateModel plateModelParsed = parse(normalized);

    return switch (plateModelParsed) {

      case PlateModel.Unparsable u -> {
        log.debug("digest: '{}' could not be parsed", normalized);
        yield u;
      }

      case PlateModel.Invalid i -> {
        log.debug("digest: '{}' did not match any plate pattern", normalized);
        yield i;
      }

      case PlateModel.Valid d when distinctIdService.find(d.distinctCode()).isEmpty() -> {
        log.debug("digest: district code '{}' not found in repo", d.distinctCode());
        yield PlateModel.Invalid.INSTANCE;
      }

      case PlateModel.Ambiguous a -> resolveAmbuiguity(a);

      case PlateModel.Valid d -> {
        log.debug("digest: valid plate -> {}", d);
        yield d;
      }
    };
  }

  private PlateModel resolveAmbuiguity(Ambiguous a) {

    Map<String, String> candidateMap = resolveIntoCandidates(a);
    String[] distinctCodeCandiates = candidateMap.keySet().toArray(new String[0]);
    List<DistinctId> knownCandidates = distinctIdService.findForAny(distinctCodeCandiates);

    if (knownCandidates.isEmpty()) {

      log.debug("digest: no known distinct code candidates found for candidates '{}'",
          Arrays.toString(distinctCodeCandiates));
      return PlateModel.Invalid.INSTANCE;

    } else if (knownCandidates.size() > 1) {

      // stays ambiguous if multiple candidates are known, as we cannot decide which
      // one is correct
      log.debug("digest: found multiple known distinct code candidates for '{}': {}", a.distinctCode(),
          knownCandidates);
      return a;

    } else {

      DistinctId knownDistinctId = knownCandidates.get(0);
      String letters = candidateMap.get(knownDistinctId.code());

      return switch (knownDistinctId) {

        case DistinctId.DistrictRecord dr -> {
          log.debug("digest: resolved ambiguous '{}' to district record '{}'", a.distinctCode(), dr.toDisplayString());

          // no district KBA plate allowed without letters, so if the resolved district
          // code candidate has no letters left, the plate is invalid
          if (letters.isEmpty()) {
            log.debug("digest: resolved letters part is empty for district plate of '{}', invalid plate",
                a.distinctCode());
            yield PlateModel.Invalid.INSTANCE;
          }
          yield new PlateModel.Valid(dr.code(), letters, a.digits());
        }

        // special plates may be valid even without letters, so we don't check for empty
        // letters here
        // FIX: root cause is that special plates have been fallen under ambiguous
        // parsing in the first place
        case DistinctId.SpecialRecord sr -> {

          PlateModel.Valid validSpecial = new PlateModel.Valid(sr.code(), letters, a.digits());

          log.debug("digest: resolved ambiguous '{}' to special plate '{}' ({}), letters rest: '{}'", a.distinctCode(),
              validSpecial.canonical());
          yield validSpecial;
        }
      };
    }
  }

  private Map<String, String> resolveIntoCandidates(PlateModel.Ambiguous a) {

    String ambiguousDistinctCode = a.distinctCode();
    int len = ambiguousDistinctCode.length();

    // LinkedHashMap preserves insertion order (longest candidate first)
    Map<String, String> candidateMap = new LinkedHashMap<>(len);
    for (int cut = 0; cut < len; cut++) {
      String candidate = ambiguousDistinctCode.substring(0, len - cut);
      String rest = ambiguousDistinctCode.substring(len - cut);

      // more than 2 letters in "Erkennungsnummer" part disqualifies the candidate
      if (rest.length() <= 2)
        candidateMap.put(candidate, rest);
    }

    log.debug("resolveIntoCandidates: resolved '{}' into candidates '{}'", ambiguousDistinctCode, candidateMap);
    return candidateMap;
  }

}
