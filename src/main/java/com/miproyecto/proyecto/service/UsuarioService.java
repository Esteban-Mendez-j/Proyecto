package com.miproyecto.proyecto.service;


import com.miproyecto.proyecto.domain.Usuario;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.EmpresaRepository;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import com.miproyecto.proyecto.util.ReferencedWarning;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;



@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CandidatoRepository candidatoRepository;
    private final EmpresaRepository empresaRepository;
    // private static final String UPLOAD_DIR = "C:/Users/Asus/Desktop/Proyecto/uploads/img/";
    // private static final String UPLOAD_DIR = System.getProperty("user.home") + "/Proyecto/uploads/img/";
    public static final String UPLOAD_DIR = Paths.get("uploads", "img").toAbsolutePath().toString();


    public UsuarioService(final UsuarioRepository usuarioRepository,
            final CandidatoRepository candidatoRepository,
            final EmpresaRepository empresaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.candidatoRepository = candidatoRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<UsuarioDTO> findAll() {
        final List<Usuario> usuarios = usuarioRepository.findAll(Sort.by("idUsuario"));
        return usuarios.stream()
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .toList();
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
        // Validar tipo de archivo
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen.");
        }

        // Crear directorio si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
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
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        // Verificar si el archivo existe antes de eliminarlo
        if (Files.exists(filePath)) {
            Files.delete(filePath); // Eliminar el archivo
        } else {
            throw new IOException("El archivo no existe: " + filePath.toString());
        }
    }

    private UsuarioDTO mapToDTO(final Usuario usuario, final UsuarioDTO usuarioDTO) {
        usuarioDTO.setIdUsuario(usuario.getIdUsuario());
        usuarioDTO.setTipo(usuario.getTipo());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setContrasena(usuario.getContrasena());
        usuarioDTO.setCorreo(usuario.getCorreo());
        usuarioDTO.setTelefono(usuario.getTelefono());
        usuarioDTO.setDescripcion(usuario.getDescripcion());
        usuarioDTO.setImagen(usuario.getImagen());
        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        usuario.setTipo(usuarioDTO.getTipo());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setDescripcion(usuarioDTO.getDescripcion());
        usuario.setImagen(usuarioDTO.getImagen());
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
