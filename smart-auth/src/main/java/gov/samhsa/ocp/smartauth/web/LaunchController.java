package gov.samhsa.ocp.smartauth.web;

import gov.samhsa.ocp.smartauth.service.LaunchService;
import gov.samhsa.ocp.smartauth.service.dto.LaunchRequestDto;
import gov.samhsa.ocp.smartauth.service.dto.LaunchResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/launches")
public class LaunchController {
    @Autowired
    private LaunchService launchService;

    @GetMapping("/{launchId}")
    public LaunchResponseDto get(@PathVariable("launchId") String launchId) {
        return launchService.get(launchId);
    }

    @PostMapping()
    public LaunchResponseDto create(@Valid @RequestBody LaunchRequestDto launchRequest) {
        return launchService.create(launchRequest);
    }

    @PostMapping("/{launchId}")
    public LaunchResponseDto mergeAndSave(@PathVariable("launchId") String launchId,
                                          @Valid @RequestBody LaunchRequestDto launchRequest) {
        return launchService.mergeAndSave(launchId, launchRequest);
    }

    @PutMapping("/{launchId}")
    public LaunchResponseDto overrideAndSave(@PathVariable("launchId") String launchId,
                                             @Valid @RequestBody LaunchRequestDto launchRequest) {
        return launchService.overrideAndSave(launchId, launchRequest);
    }
}
