package gov.samhsa.ocp.smartcore.service;

import gov.samhsa.ocp.smartcore.config.SmartCoreProperties;
import gov.samhsa.ocp.smartcore.infrastructure.OAuth2ClientRestClient;
import gov.samhsa.ocp.smartcore.infrastructure.dto.ClientDto;
import gov.samhsa.ocp.smartcore.infrastructure.dto.ClientMetaDto;
import gov.samhsa.ocp.smartcore.service.dto.ClientDetailDto;
import gov.samhsa.ocp.smartcore.service.dto.ClientType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private OAuth2ClientRestClient oAuth2ClientRestClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SmartCoreProperties smartCoreProperties;

    @Override
    public List<ClientMetaDto> getAllClientMeta() {
        return oAuth2ClientRestClient.getAllClientMeta().stream()
                .filter(meta -> StringUtils.hasText(meta.getAppLaunchUrl()))
                .collect(Collectors.toList());
    }
    @Override
    public void createClient(ClientDetailDto clientDetailDto) {
        if(clientDetailDto.getClient_type().equals(ClientType.PUBLIC)){
            clientDetailDto.setClient_secret(smartCoreProperties.getPublicClientSecret());
        }
        final ClientDto clientDto = oAuth2ClientRestClient.createClient(modelMapper.map(clientDetailDto, ClientDto.class));
        final ClientMetaDto clientMetaDto = oAuth2ClientRestClient.createClientMeta(clientDto.getClient_id(),modelMapper.map(clientDetailDto, ClientMetaDto.class));
    }

    @Override
    public void updateClient(String clientId, ClientDetailDto clientDetailDto) {
        if(clientDetailDto.getClient_type().equals(ClientType.PUBLIC)){
            clientDetailDto.setClient_secret(smartCoreProperties.getPublicClientSecret());
        }
        final ClientDto clientDto = oAuth2ClientRestClient.updateClient(clientId, modelMapper.map(clientDetailDto, ClientDto.class));
        final ClientMetaDto clientMetaDto = oAuth2ClientRestClient.createClientMeta(clientDto.getClient_id(),modelMapper.map(clientDetailDto, ClientMetaDto.class));
    }

    @Override
    public void deleteClient(String clientId) {
        oAuth2ClientRestClient.deleteClient(clientId);
    }

    @Override
    public List<ClientDetailDto> getAllClients() {
        List<ClientMetaDto> clientMetaDtos = oAuth2ClientRestClient.getAllClientMeta().stream()
                .filter(meta -> StringUtils.hasText(meta.getAppLaunchUrl()))
                .collect(Collectors.toList());

        List<ClientDto> clientDtos = oAuth2ClientRestClient.getAllClients().getResources();

        List<ClientDetailDto> clientDetailDtos = new LinkedList<>();

        clientMetaDtos.stream().forEach(clientMetaDto -> {
            ClientDetailDto clientDetailDto = modelMapper.map(clientDtos.stream().filter(clientDto -> clientDto.getClient_id().equals(clientMetaDto.getClientId())).findFirst(), ClientDetailDto.class);
            clientDetailDto.setAppIcon(clientMetaDto.getAppIcon());
            clientDetailDto.setAppLaunchUrl(clientMetaDto.getAppLaunchUrl());
            clientDetailDtos.add(clientDetailDto);
        });
        return clientDetailDtos;

    }
}
