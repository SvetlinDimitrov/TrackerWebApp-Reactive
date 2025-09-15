package org.nutriGuideBuddy.web;

import lombok.RequiredArgsConstructor;
import org.nutriGuideBuddy.features.record.dto.CreateRecord;
import org.nutriGuideBuddy.features.record.dto.RecordView;
import org.nutriGuideBuddy.features.record.service.RecordService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/record")
@PreAuthorize("@userDetailsAccessValidator.isFullyRegistered()")
public class RecordController {

  private final RecordService service;

  @PostMapping
  public Mono<RecordView> viewRecord(@RequestBody CreateRecord dto) {
    return service.viewRecord(dto);
  }
}
