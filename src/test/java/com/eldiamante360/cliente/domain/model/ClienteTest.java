package com.eldiamante360.cliente.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteTest {

    private final Ruta ruta = Ruta.nueva("Ruta Norte", "Zona norte de la ciudad");

    @Test
    void nuevo_creaClienteActivoSinRutaObligatoria() {
        Cliente cliente = Cliente.nuevo(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        assertThat(cliente.isActivo()).isTrue();
        assertThat(cliente.getId()).isNull();
        assertThat(cliente.getVersion()).isNull();
        assertThat(cliente.getRuta()).isNull();
    }

    @Test
    void nuevo_conRuta_asignaLaRuta() {
        Cliente cliente = Cliente.nuevo(TipoDocumentoCliente.NIT, "900123456", "Distribuidora XYZ",
                List.of("3009876543"), "contacto@xyz.com", "Carrera 10 # 20-30", ruta);

        assertThat(cliente.getRuta()).isEqualTo(ruta);
    }

    @Test
    void actualizarDatos_cambiaCamposEditables() {
        Cliente cliente = Cliente.nuevo(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        cliente.actualizarDatos("Juan Perez Gomez", List.of("3007654321"), "juan.gomez@correo.com", "Calle 5 # 6-7");

        assertThat(cliente.getNombre()).isEqualTo("Juan Perez Gomez");
        assertThat(cliente.getTelefonos()).containsExactly("3007654321");
        assertThat(cliente.getEmail()).isEqualTo("juan.gomez@correo.com");
        assertThat(cliente.getDireccion()).isEqualTo("Calle 5 # 6-7");
    }

    @Test
    void asignarRuta_conRutaNoNula_asignaLaRuta() {
        Cliente cliente = Cliente.nuevo(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        cliente.asignarRuta(ruta);

        assertThat(cliente.getRuta()).isEqualTo(ruta);
    }

    @Test
    void asignarRuta_conNull_removueLaRuta() {
        Cliente cliente = Cliente.nuevo(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", ruta);

        cliente.asignarRuta(null);

        assertThat(cliente.getRuta()).isNull();
    }

    @Test
    void activarYDesactivar_cambianElEstado() {
        Cliente cliente = Cliente.nuevo(TipoDocumentoCliente.CC, "123456789", "Juan Perez",
                List.of("3001234567"), "juan@correo.com", "Calle 1 # 2-3", null);

        cliente.desactivar();
        assertThat(cliente.isActivo()).isFalse();

        cliente.activar();
        assertThat(cliente.isActivo()).isTrue();
    }
}
