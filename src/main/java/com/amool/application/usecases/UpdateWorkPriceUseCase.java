package com.amool.application.usecases;

import com.amool.application.port.out.ObtainWorkByIdPort;
import com.amool.application.port.out.WorkPort;
import com.amool.domain.model.Work;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Objects;

public class UpdateWorkPriceUseCase {

    private final ObtainWorkByIdPort obtainWorkByIdPort;
    private final WorkPort workPort;

    public UpdateWorkPriceUseCase(ObtainWorkByIdPort obtainWorkByIdPort, WorkPort workPort) {
        this.obtainWorkByIdPort = obtainWorkByIdPort;
        this.workPort = workPort;
    }

    public void execute(Long workId, BigDecimal price, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("Usuario no autenticado");
        }
        if (price == null) {
            throw new IllegalArgumentException("Precio inválido");
        }

        Work work = obtainWorkByIdPort
                .obtainWorkById(workId)
                .orElseThrow(() -> new NoSuchElementException("Obra no encontrada"));

        if (work.getCreator() == null || !Objects.equals(work.getCreator().getId(), authenticatedUserId)) {
            throw new SecurityException("No autorizado para modificar esta obra");
        }

        work.setPrice(price);
        workPort.updateWork(work);
    }
}
