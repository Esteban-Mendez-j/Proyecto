package com.miproyecto.proyecto.service;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.miproyecto.proyecto.domain.Usuario;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.repos.RolesRepository;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.repos.UsuarioSpecifications;
import com.miproyecto.proyecto.util.NotFoundException;



@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolesRepository rolesRepository;
    // public static final String UPLOAD_DIR = Path.of("uploads", "img").toAbsolutePath().toString();
    
    @Value("${app.upload-dir.img}")
    private String imgUploadDir;

    @Value("${app.upload-dir.pdf}")
    private String pdfUploadDir;

    public UsuarioService(UsuarioRepository usuarioRepository, RolesRepository rolesRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolesRepository = rolesRepository;
    }

    public List<UsuarioDTO> findAll() {
        final List<Usuario> usuarios = usuarioRepository.findAll(Sort.by("idUsuario"));
        return usuarios.stream()
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .toList();
    }

    public List<UsuarioDTO> findAllByBannedStatus(Boolean isBanned, Long idUsuario, Pageable pageable) {
        Page<Usuario> usuarios = usuarioRepository.findByIsActive(isBanned, pageable);
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
            .map(usuario -> mapToDTO(usuario, new UsuarioDTO())).orElse(null);
        
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

    public String guardarArchivo(MultipartFile file, Long idUsuario) throws IOException {
        String tipo = file.getContentType();

        // Verifica si es imagen o PDF
        String carpeta;
        if (tipo != null && tipo.startsWith("image/")) {
            carpeta = imgUploadDir;
        } else if ("application/pdf".equals(tipo)) {
            carpeta = pdfUploadDir;
        } else {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen o PDF.");
        }

        // Crear carpeta si no existe
        Path rutaCarpeta = Path.of(carpeta).toAbsolutePath();
        Files.createDirectories(rutaCarpeta);

        // Crear nombre único para el archivo
        String nombreArchivo = idUsuario + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);

        // Guardar el archivo en el servidor
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
        }

        return nombreArchivo;
    }

    public void eliminarArchivo(String fileName, boolean esImagen) throws IOException {
        // Determinar la carpeta dependiendo si es imagen o PDF
        String carpeta = esImagen ? imgUploadDir : pdfUploadDir;
        Path ruta = Path.of(carpeta, fileName);

        // Eliminar el archivo si existe
        if (Files.exists(ruta)) {
            Files.delete(ruta);
        } else {
            throw new IOException("El archivo no existe: " + ruta);
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
        usuarioDTO.setFechaInicioSesion(usuario.getFechaInicioSesion());
        usuarioDTO.setFechaRegistro(usuario.getFechaRegistro());
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
        usuario.setFechaInicioSesion(usuarioDTO.getFechaInicioSesion());
        usuario.setFechaRegistro(usuarioDTO.getFechaRegistro());
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

    // public Map<String,Object> findUserByEstado(boolean estado, Pageable pageable, String nameList) {
    //     final Page<Usuario> usuarios =  usuarioRepository.findIsActiveUsuariosByIsActive(estado, pageable);
    //     final Page<UsuarioDTO> usuariosDTO = usuarios.map(usuario -> mapToDTO(usuario, new VacanteDTO()));
    //     return mapResponse(usuariosDTO, nameList) ;       
    // }





    public Map<String,Object> mapResponse(Page<UsuarioDTO> pageableResponse, String nameList){
        Map<String,Object> response = new HashMap<>();

        response.put(nameList, pageableResponse.getContent());
        response.put("totalElements", pageableResponse.getTotalElements());
        response.put("pageActual", pageableResponse.getPageable());
        response.put("totalPage", pageableResponse.getTotalPages());
        return response;
    }
    // public Map<String,Object> findByIdUsuario(Long idUsuario, Pageable pageable) {
    //     // Obtener la empresa usando su id
    //     Empresa empresa = empresaRepository.findById(idUsuario)
    //             .orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
        
    //     // Obtener las vacantes relacionadas con esa empresa
    //     Page<Vacante> vacantes = vacanteRepository.findByIdUsuario(empresa, pageable);
        
    //     // Convertir cada vacante a VacanteDTO y devolver la lista
    //     Page<VacanteDTO> vacantesDTO = vacantes.map(vacante -> mapToDTO(vacante, new VacanteDTO()));
    //     return mapResponse(vacantesDTO, "vacantes");
                
    // }

    
//  buscar usuarios Activos o inactivos
//     public Map<String,Object> findAllByEstado(boolean estado, Pageable pageable, String nameList) {
//         final Page<Usuario> usuarios = usuarioRepository.findByIsActive(estado, pageable);
//         final Page<UsuarioDTO> usuarioDTO = usuarios.map(Usuario -> mapToDTO(Usuario, new UsuarioDTO()));
//         return mapResponse(usuarioDTO, nameList) ;       
//     }
    //FiltrosUsuarios
    public Map<String, Object> buscarUsuariosConFiltros(Long idUsuario,String rolUsuario,String nombre , String rol, Boolean estado  , Pageable pageable) {
        Specification<Usuario> specification = UsuarioSpecifications.conFiltros(rolUsuario,nombre,  rol,  estado);
        
        Page<UsuarioDTO> page = usuarioRepository.findAll(specification, pageable)
        .map(usuario -> mapToDTO(usuario, new UsuarioDTO())); 
        
        return mapResponse(page, "usuarios");  
    }
}