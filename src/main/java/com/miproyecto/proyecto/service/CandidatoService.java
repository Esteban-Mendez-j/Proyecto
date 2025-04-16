package com.miproyecto.proyecto.service;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Estudio;
import com.miproyecto.proyecto.domain.HistorialLaboral;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Roles;
import com.miproyecto.proyecto.model.CandidatoDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.EstudioRepository;
import com.miproyecto.proyecto.repos.HistorialLaboralRepository;
import com.miproyecto.proyecto.repos.PostuladoRepository;
import com.miproyecto.proyecto.repos.RolesRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import com.miproyecto.proyecto.util.ReferencedWarning;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CandidatoService{

    private final CandidatoRepository candidatoRepository;
    private final PostuladoRepository postuladoRepository;
    private final EstudioRepository estudioRepository;
    private final HistorialLaboralRepository historialLaboralRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

   

    public CandidatoService(CandidatoRepository candidatoRepository, PostuladoRepository postuladoRepository,
            EstudioRepository estudioRepository, HistorialLaboralRepository historialLaboralRepository,
            PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {
        this.candidatoRepository = candidatoRepository;
        this.postuladoRepository = postuladoRepository;
        this.estudioRepository = estudioRepository;
        this.historialLaboralRepository = historialLaboralRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
    }

    // busca un candidato por su id
    public CandidatoDTO get(final Long idUsuario) {
        return candidatoRepository.findById(idUsuario)
                .map(candidato -> mapToDTO(candidato, new CandidatoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    // busca un candidato por su identificacion
    public CandidatoDTO getByIdentificacion(final String identificacion) {
        return candidatoRepository.findByIdentificacion(identificacion)
                .map(candidato -> mapToDTO(candidato, new CandidatoDTO()))
                .orElseThrow(NotFoundException::new);
    }


    // crea y guarda un objeto candidato en la base de datos 
    public void create(final CandidatoDTO candidatoDTO) {
        final Candidato candidato = new Candidato();
        List<Roles> roles= new ArrayList<>();

        roles.add(rolesRepository.findByRol("CANDIDATO"));
        candidatoDTO.setIsActive(true);
        mapToEntity(candidatoDTO, candidato, true);
        candidato.setRoles(roles);// guarda el rol en la db
         
        candidatoRepository.save(candidato);
    }

    
    // busca y actualiza un objeto candidato en la base de datos 
    public void update(final Long idUsuario, final CandidatoDTO candidatoDTO) {
        final Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        mapToEntity(candidatoDTO, candidato, false);
        candidatoRepository.save(candidato);
    }

    public void delete(final Long idUsuario) {
        candidatoRepository.deleteById(idUsuario);
    }

    // convierte un objeto de tipo candidato a candidatoDTO
    public CandidatoDTO mapToDTO(final Candidato candidato, final CandidatoDTO candidatoDTO) {
        candidatoDTO.setIdUsuario(candidato.getIdUsuario());
        candidatoDTO.setNombre(candidato.getNombre());
        candidatoDTO.setContrasena(candidato.getContrasena());
        candidatoDTO.setCorreo(candidato.getCorreo());
        candidatoDTO.setTelefono(candidato.getTelefono());
        candidatoDTO.setDescripcion(candidato.getDescripcion());
        candidatoDTO.setImagen(candidato.getImagen());
        candidatoDTO.setApellido(candidato.getApellido());
        candidatoDTO.setCurriculo(candidato.getCurriculo());
        candidatoDTO.setExperiencia(candidato.getExperiencia());
        candidatoDTO.setIdentificacion(candidato.getIdentificacion());
        candidatoDTO.setIsActive(candidato.getIsActive());
        candidatoDTO.setComentarioAdmin(candidato.getComentarioAdmin());

        candidatoDTO.setRoles(
            candidato.getRoles().stream()
                    .map(roles -> roles.getRol())
                    .collect(Collectors.toList())
        );
        return candidatoDTO;
    }

    // convierte un objeto de Roles candidatoDTO a candidato
    private Candidato mapToEntity(final CandidatoDTO candidatoDTO, final Candidato candidato, boolean crear) {
        if (crear) {
            // candidato.setRoles(candidatoDTO.getRoles());
            candidato.setContrasena(passwordEncoder.encode(candidatoDTO.getContrasena()));
        }
        
        candidato.setNombre(candidatoDTO.getNombre());
        candidato.setCorreo(candidatoDTO.getCorreo());
        candidato.setTelefono(candidatoDTO.getTelefono());
        candidato.setDescripcion(candidatoDTO.getDescripcion());
        candidato.setImagen(candidatoDTO.getImagen());
        candidato.setApellido(candidatoDTO.getApellido());
        candidato.setCurriculo(candidatoDTO.getCurriculo());
        candidato.setExperiencia(candidatoDTO.getExperiencia());
        candidato.setIdentificacion(candidatoDTO.getIdentificacion());
        candidato.setIsActive(candidatoDTO.getIsActive());
        candidato.setComentarioAdmin(candidatoDTO.getComentarioAdmin());
        return candidato;
    }

    public boolean identificacionExists(final String identificacion) {
        return candidatoRepository.existsByIdentificacionIgnoreCase(identificacion);
    }

    public boolean estudiosExist(final Long idUsuario) {
        final Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        return estudioRepository.existsByIdUsuario(candidato);
    }
    

    public boolean idUsuarioExists(final Long idUsuario) {
        return candidatoRepository.existsByIdUsuario(idUsuario);
    }

    public ReferencedWarning getReferencedWarning(final Long idUsuario) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        
        // Verificamos si hay alguna referencia en las otras tablas.
        final Postulado postulado = postuladoRepository.findFirstByIdUsuario(candidato);
        if (postulado != null) {
            referencedWarning.setKey("candidato.postulado.idUsuario.referenced");
            referencedWarning.addParam(postulado.getNPostulacion());
            return referencedWarning;
        }

        final Estudio estudio = estudioRepository.findFirstByIdUsuario(candidato);
        if (estudio != null) {
            referencedWarning.setKey("candidato.estudio.idUsuario.referenced");
            referencedWarning.addParam(estudio.getIdEstudio());
            return referencedWarning;
        }

        final HistorialLaboral historialLaboral = historialLaboralRepository.findFirstByIdUsuario(candidato);
        if (historialLaboral != null) {
            referencedWarning.setKey("candidato.historialLaboral.idUsuario.referenced");
            referencedWarning.addParam(historialLaboral.getIDHistorial());
            return referencedWarning;
        }

        return null;
    }
}

