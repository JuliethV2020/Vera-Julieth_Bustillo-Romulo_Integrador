package com.clinica.clinica.service.imple;

import com.clinica.clinica.dto.entrada.modificacion.OdontologoModificacionEntradaDto;
import com.clinica.clinica.dto.entrada.odontologo.OdontologoEntradaDto;
import com.clinica.clinica.dto.salida.odontologo.OdontologoSalidaDto;
import com.clinica.clinica.entity.Odontologo;
import com.clinica.clinica.exceptions.ResourceNotFoundException;
import com.clinica.clinica.repository.OdontologoRepository;
import com.clinica.clinica.service.IOdontologoService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OdontologoService implements IOdontologoService {

    private final Logger LOGGER = LoggerFactory.getLogger(OdontologoService.class);
    private final OdontologoRepository odontologoRepository;
    private final ModelMapper modelMapper;

    public OdontologoService(OdontologoRepository odontologoRepository, ModelMapper modelMapper) {
        this.odontologoRepository = odontologoRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<OdontologoSalidaDto> listarOdontologos() {
        List<OdontologoSalidaDto> odontologos = odontologoRepository.findAll().stream()
                .map(o -> modelMapper.map(o, OdontologoSalidaDto.class)).toList();

        LOGGER.info("Listado de todos los odontologos: {}", odontologos);

        return odontologos;
    }

    @Override
    public OdontologoSalidaDto registrarOdontologo(OdontologoEntradaDto odontologo) {
        Odontologo odGuardado = odontologoRepository.save(dtoEntradaAEntidad(odontologo));
        OdontologoSalidaDto odontologoSalidaDto = modelMapper.map(odGuardado, OdontologoSalidaDto.class);
        if (odontologo.getMatricula().length()<10){
            LOGGER.info("Odontologo No se pudo guardar porque matricula es menor a 10");
        }else{
            LOGGER.info("Odontologo guardado: {}", odontologoSalidaDto);
        }
        return odontologoSalidaDto;
    }

    @Override
    public OdontologoSalidaDto buscarOdontologoPorId(Long id) {
        Odontologo odontologoBuscado = odontologoRepository.findById(id).orElse(null);

        OdontologoSalidaDto odontologoSalidaDto = null;
        if (odontologoBuscado != null) {
            odontologoSalidaDto = modelMapper.map(odontologoBuscado, OdontologoSalidaDto.class);
            LOGGER.info("Odontologo encontrado: {}", odontologoSalidaDto);
        } else LOGGER.error("El id no se encuentra registrado en la base de datos");

        return odontologoSalidaDto;
    }

    @Override
    public void eliminarOdontologo(Long id) throws ResourceNotFoundException {
        if (buscarOdontologoPorId(id) != null) {
            odontologoRepository.deleteById(id);
            LOGGER.warn("Se ha eliminado el odontologo con id: {}", id);
        } else {
            LOGGER.error("No se ha encontrado el odontologo con id {}", id);
            throw new ResourceNotFoundException("No se ha encontrado el odontologo con id " + id);
        }
    }

    @Override
    public OdontologoSalidaDto actualizarOdontologo(OdontologoModificacionEntradaDto odontologoModificacionEntradaDto) throws ResourceNotFoundException {
        Odontologo odontologoRecibido = modelMapper.map(odontologoModificacionEntradaDto, Odontologo.class);
        Odontologo odontologoAActualizar = odontologoRepository.findById(odontologoModificacionEntradaDto.getId()).orElse(null);
        OdontologoSalidaDto odontologoSalidaDto = null;

        if (odontologoAActualizar != null) {

            odontologoAActualizar = odontologoRecibido;
            odontologoRepository.save(odontologoAActualizar);

            odontologoSalidaDto = modelMapper.map(odontologoAActualizar, OdontologoSalidaDto.class);

            LOGGER.warn("Odontologo actualizado: {}", odontologoSalidaDto);

        } else {
            LOGGER.error("No fue posible actualizar los datos ya que el odontologo no se encuentra registrado");
            throw new ResourceNotFoundException("No fue posible actualizar los datos ya que el odontologo no se encuentra registrado");
        }


        return odontologoSalidaDto;
    }

    private Odontologo dtoEntradaAEntidad(OdontologoEntradaDto odontologoEntradaDto) {
        return modelMapper.map(odontologoEntradaDto, Odontologo.class);
    }
}
