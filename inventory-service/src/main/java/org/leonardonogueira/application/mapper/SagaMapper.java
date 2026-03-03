package org.leonardonogueira.application.mapper;

import org.leonardonogueira.application.dto.Event;
import org.leonardonogueira.avro.command.SagaEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SagaMapper {

    // Converte de Avro para seu Modelo de Domínio (Wrapper)
    Event toDomain(SagaEvent avroEvent);

    // Converte de Domínio para Avro (para enviar ao Kafka)
    SagaEvent toAvro(Event domain);
}
