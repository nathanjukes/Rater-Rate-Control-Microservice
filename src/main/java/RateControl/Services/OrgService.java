package RateControl.Services;

import RateControl.Exceptions.BadRequestException;
import RateControl.Exceptions.InternalServerException;
import RateControl.Models.Org.Org;
import RateControl.Models.Org.OrgCreateRequest;
import RateControl.Repositories.OrgRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrgService {
    private static final Logger log = LogManager.getLogger(OrgService.class);

    private OrgRepository orgRepository;

    @Autowired
    public OrgService(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    public Optional<Org> createOrg(OrgCreateRequest orgCreateRequest) throws InternalServerException, BadRequestException {
        Org org = Org.from(orgCreateRequest);

        if (orgRepository.existsByName(org.getName())) {
            log.info("Org Create Denied - Duplicate Name: " + org.getName());
            throw new BadRequestException();
        }

        try {
            return Optional.of(orgRepository.save(org));
        } catch (Exception ex) {
            throw new InternalServerException();
        }
    }

    public Optional<Org> getOrg(UUID orgId) {
        return orgRepository.findById(orgId);
    }

    public Optional<Org> getOrg(String orgName) {
        return orgRepository.findByName(orgName);
    }

    public Optional<List<Org>> getOrgs() {
        return Optional.of(orgRepository.findAll());
    }

    public void deleteOrg(UUID orgId) {
        orgRepository.deleteById(orgId);
    }
}
