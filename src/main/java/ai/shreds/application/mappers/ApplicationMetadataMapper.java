package ai.shreds.application.mappers;

import ai.shreds.domain.entities.*;
import ai.shreds.domain.value_objects.*;
import ai.shreds.shared.dtos.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMetadataMapper {

    @Mapping(source = "checksum", target = "checksum", qualifiedByName = "stringToChecksum")
    @Mapping(source = "ownershipDetails", target = "ownershipDetails", qualifiedByName = "stringToOwnership")
    @Mapping(source = "creationTimestamp", target = "creationTimestamp", qualifiedByName = "stringToDate")
    @Mapping(source = "lastModifiedTimestamp", target = "lastModifiedTimestamp", qualifiedByName = "stringToDate")
    DomainEntityFileMetadata toDomainEntity(SharedFileMetadataCreateUpdateRequestDTO dto);

    @Mapping(source = "checksum.checksumValue", target = "checksum")
    @Mapping(source = "ownershipDetails.owner", target = "ownershipDetails")
    @Mapping(source = "creationTimestamp", target = "creationTimestamp", qualifiedByName = "dateToString")
    @Mapping(source = "lastModifiedTimestamp", target = "lastModifiedTimestamp", qualifiedByName = "dateToString")
    SharedFileMetadataDTO toDTO(DomainEntityFileMetadata entity);

    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "dateToString")
    @Mapping(source = "checksum.checksumValue", target = "versionChecksum")
    SharedVersionRecordDTO toVersionDTO(DomainEntityVersionRecord entity);

    List<SharedVersionRecordDTO> toVersionDTOList(List<DomainEntityVersionRecord> entities);

    @Named("stringToChecksum")
    default DomainValueChecksum stringToChecksum(String value) {
        return value != null ? new DomainValueChecksum(value) : null;
    }

    @Named("stringToOwnership")
    default DomainValueOwnership stringToOwnership(String value) {
        return value != null ? new DomainValueOwnership(value) : null;
    }

    @Named("stringToDate")
    default Date stringToDate(String value) {
        if (value == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(value);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format: " + value);
        }
    }

    @Named("dateToString")
    default String dateToString(Date date) {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date) : null;
    }
}
