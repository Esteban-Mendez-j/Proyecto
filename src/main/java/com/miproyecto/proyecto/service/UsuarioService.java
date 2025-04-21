package com.miproyecto.proyecto.service;


import com.miproyecto.proyecto.domain.Usuario;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.EmpresaRepository;
import com.miproyecto.proyecto.repos.RolesRepository;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import com.miproyecto.proyecto.util.ReferencedWarning;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;



@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CandidatoRepository candidatoRepository;
    private final EmpresaRepository empresaRepository;
    private final RolesRepository rolesRepository;
    public static final String UPLOAD_DIR = Path.of("uploads", "img").toAbsolutePath().toString();

    public UsuarioService(UsuarioRepository usuarioRepository, CandidatoRepository candidatoRepository,
            EmpresaRepository empresaRepository, RolesRepository rolesRepository) {
        this.usuarioRepository = usuarioRepository;
        this.candidatoRepository = candidatoRepository;
        this.empresaRepository = empresaRepository;
        this.rolesRepository = rolesRepository;
    }

    public List<UsuarioDTO> findAll() {
        final List<Usuario> usuarios = usuarioRepository.findAll(Sort.by("idUsuario"));
        return usuarios.stream()
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .toList();
    }

    public List<UsuarioDTO> findAllByBannedStatus(Boolean isBanned, Long idUsuario) {
        List<Usuario> usuarios = usuarioRepository.findByIsActive(isBanned);
        Usuario usuarioAutenticado = usuarioRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
    
        boolean esSuperAdmin = usuarioAutenticado.getRoles().stream()
                .anyMatch(rol -> rol.getRol().equals("SUPER_ADMIN"));
    
        if (esSuperAdmin) {
            return usuarios.stream()
                    .filter(usuario -> !usuario.getIdUsuario().equals(idUsuario))
                    .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                    .toList();
        } else {
            return usuarios.stream()
                    .filter(usuario -> !usuario.getIdUsuario().equals(idUsuario))
                    .filter(usuario -> usuario.getRoles().stream().noneMatch(rol ->
                            rol.getRol().equals("ADMIN") || rol.getRol().equals("SUPER_ADMIN")))
                    .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                    .toList();
        }
    }
    
    
    public Long findIdByCorreo(String correo){
        Usuario usuario = usuarioRepository.getByCorreo(correo)
            .orElseThrow(NotFoundException::new);
        return usuario.getIdUsuario();
    }

    public UsuarioDTO findByCorreo(String correo){
        return usuarioRepository.getByCorreo(correo)
            .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
            .orElseThrow(NotFoundException::new);
        
    }

    public Optional<Usuario> findByCorreo(String correo, boolean isAutenticacion){
        try {
            return usuarioRepository.getByCorreo(correo);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
        
    }

    public UsuarioDTO get(final Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Optional<UsuarioDTO> getByCorreoAndContrasena(final String correo, final String contrasena) {
        // Busca el usuario por correo
        return usuarioRepository.getByCorreo(correo)
                .filter(usuario -> usuario.getContrasena().equals(contrasena))  // Verifica la contraseña
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()));           // Mapea a DTO si las credenciales son correctas
    }
    
    public boolean esUsuarioValido(String correo, String contrasena) { /// nuevo buscar usuario alex
        return usuarioRepository.findByCorreoAndContrasena(correo, contrasena).isPresent();
    }

    public Long create(final UsuarioDTO usuarioDTO) {
        final Usuario usuario = new Usuario();
        mapToEntity(usuarioDTO, usuario);
        return usuarioRepository.save(usuario).getIdUsuario();
    }

    public void update(final Long idUsuario, final UsuarioDTO usuarioDTO) {
        final Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usuarioDTO, usuario);
        usuarioRepository.save(usuario);
    }

    public void delete(final Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }


    public String guardarImagen(MultipartFile file, Long idUsuario) throws IOException {
        // Validar Roles de archivo
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen.");
        }

        // Crear directorio si no existe
        Path uploadPath = Path.of(UPLOAD_DIR);
        Files.createDirectories(uploadPath); // Crear directorios si no existen
        // Generar un nombre único para el archivo
        String nombreArchivo = idUsuario + "_" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path rutaArchivo = uploadPath.resolve(nombreArchivo);

        // Guardar el archivo en el servidor
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
        }

        // Retornar el nombre único del archivo (puede usarse para acceder a la imagen más tarde)
        return nombreArchivo;
    }

    public void eliminarImagen(String fileName) throws IOException {
        // Construir la ruta completa del archivo usando el nombre de la imagen
        Path filePath = Path.of(UPLOAD_DIR, fileName);

        // Verificar si el archivo existe antes de eliminarlo
        if (Files.exists(filePath)) {
            Files.delete(filePath); // Eliminar el archivo
        } else {
            throw new IOException("El archivo no existe: " + filePath.toString());
        }
    }

    private UsuarioDTO mapToDTO(final Usuario usuario, final UsuarioDTO usuarioDTO) {
        usuarioDTO.setIdUsuario(usuario.getIdUsuario());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setContrasena(usuario.getContrasena());
        usuarioDTO.setCorreo(usuario.getCorreo());
        usuarioDTO.setTelefono(usuario.getTelefono());
        usuarioDTO.setDescripcion(usuario.getDescripcion());
        usuarioDTO.setImagen(usuario.getImagen());
        usuarioDTO.setIsActive(usuario.getIsActive());
        usuarioDTO.setComentarioAdmin(usuario.getComentarioAdmin());
        usuarioDTO.setRoles(
            usuario.getRoles().stream()
                .map(roles -> roles.getRol())
                .collect(Collectors.toList())
        );

        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setDescripcion(usuarioDTO.getDescripcion());
        usuario.setImagen(usuarioDTO.getImagen());
        usuario.setIsActive(usuarioDTO.getIsActive());
        usuario.setComentarioAdmin(usuarioDTO.getComentarioAdmin());
        usuario.setRoles(
            usuarioDTO.getRoles().stream()
                    .map(roles -> rolesRepository.findByRol(roles))
                    .collect(Collectors.toList())
        );
        return usuario;
    }

    public boolean correoExists(final String correo) {
        return usuarioRepository.existsByCorreoIgnoreCase(correo);
    }

    public boolean telefonoExists(final String telefono) {
        return usuarioRepository.existsByTelefonoIgnoreCase(telefono);
    }


    public ReferencedWarning getReferencedWarning(final Long idUsuario) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
    
        if (candidatoRepository.existsByIdUsuario(usuario.getIdUsuario())) {
            referencedWarning.setKey("usuario.candidato.idUsuario.referenced");
            referencedWarning.addParam(idUsuario); // Usa idUsuario directamente.
            return referencedWarning;
        }
    
        if (empresaRepository.existsByIdUsuario(usuario.getIdUsuario())) {
            referencedWarning.setKey("usuario.empresa.idUsuario.referenced");
            referencedWarning.addParam(idUsuario); // Usa idUsuario directamente.
            return referencedWarning;
        }
    
        return null;
    }
    

}
