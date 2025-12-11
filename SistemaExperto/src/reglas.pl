% Declaramos que estos hechos serán dinámicos
:- dynamic enfermedad/3.      % enfermedad(Nombre, Categoria, Recomendacion)
:- dynamic sintoma_de/2.      % sintoma_de(NombreEnfermedad, Sintoma)


% Regla para limpiar la base de conocimiento antes de una nueva carga desde Java
limpiar_conocimiento :-
    retractall(enfermedad(_,_,_)),
    retractall(sintoma_de(_,_)).

% coincide_sintomas: Verdadero si TODOS los síntomas del paciente están en la lista de la enfermedad.
coincide_sintomas_todos(Enfermedad, ListaSintomasPaciente) :-
    enfermedad(Enfermedad, _, _),
    forall(member(Sintoma, ListaSintomasPaciente), sintoma_de(Enfermedad, Sintoma)).


% diagnostico: Devuelve una enfermedad que coincide con AL MENOS UN síntoma.
% (Esta es la lógica que ya estás usando en GestorDiagnostico.java)
diagnostico(Enfermedad, Categoria, Recomendacion, ListaSintomasPaciente) :-
    enfermedad(Enfermedad, Categoria, Recomendacion),
    member(Sintoma, ListaSintomasPaciente),
    sintoma_de(Enfermedad, Sintoma).

% diagnostico_categoria: Devuelve enfermedades de una categoría específica.
% Uso en Java: Query("diagnostico_categoria(Enf, 'viral', Rec)").
diagnostico_categoria(Enfermedad, Categoria, Recomendacion) :-
    enfermedad(Enfermedad, Categoria, Recomendacion).

% recomendacion: Devuelve la recomendación asociada a la enfermedad.
% Uso en Java: Query("recomendacion_de('gripe', Rec)").
recomendacion_de(Enfermedad, Recomendacion) :-
    enfermedad(Enfermedad, _, Recomendacion).

% enfermedades_cronicas: Devuelve la lista de todas las enfermedades crónicas.
% Uso en Java: Query("enfermedades_cronicas(Enf, Cat, Rec)").
enfermedades_cronicas(Enfermedad, Categoria, Recomendacion) :-
    enfermedad(Enfermedad, Categoria, Recomendacion),
    Categoria = 'crónica'. % Asegúrate que el valor en tu BD esté en minúsculas.

% enfermedades_por_sintoma: Devuelve todas las enfermedades que presentan un síntoma específico.
% Uso en Java: Query("enfermedades_por_sintoma(Enf, 'fiebre')").
enfermedades_por_sintoma(Enfermedad, Sintoma) :-
    enfermedad(Enfermedad, _, _),
<<<<<<< Updated upstream
    sintoma_de(Enfermedad, Sintoma).
=======
    sintoma_de(Enfermedad, Sintoma).

% Busca un diagnóstico que cumpla con los síntomas Y cuya categoría coincida con el filtro.
diagnostico_filtrado(Enfermedad, Categoria, Recomendacion, ListaSintomasPaciente, CategoriaFiltro) :-
    % 1. Encontrar un diagnóstico base (coincidencia de al menos un síntoma)
    diagnostico(Enfermedad, Categoria, Recomendacion, ListaSintomasPaciente),
    
    % 2. Aplicar el filtro de categoría (unificación)
    Categoria = CategoriaFiltro.

>>>>>>> Stashed changes
