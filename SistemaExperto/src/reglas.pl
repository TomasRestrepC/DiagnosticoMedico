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
diagnostico(Enfermedad, Categoria, Recomendacion, ListaSintomasPaciente) :-
    enfermedad(Enfermedad, Categoria, Recomendacion),
    member(Sintoma, ListaSintomasPaciente),
    sintoma_de(Enfermedad, Sintoma).

% diagnostico_categoria: Devuelve enfermedades de una categoría específica.
diagnostico_categoria(Enfermedad, Categoria, Recomendacion) :-
    enfermedad(Enfermedad, Categoria, Recomendacion).

% recomendacion: Devuelve la recomendación asociada a la enfermedad.
recomendacion_de(Enfermedad, Recomendacion) :-
    enfermedad(Enfermedad, _, Recomendacion).

% enfermedades_cronicas: Devuelve la lista de todas las enfermedades crónicas.
enfermedades_cronicas(Enfermedad, Categoria, Recomendacion) :-
    enfermedad(Enfermedad, Categoria, Recomendacion),
    Categoria = 'crónica'. % Asegúrate que el valor en tu BD esté en minúsculas.

% enfermedades_por_sintoma: Devuelve todas las enfermedades que presentan un síntoma específico.
enfermedades_por_sintoma(Enfermedad, Sintoma) :-
    enfermedad(Enfermedad, _, _),

    sintoma_de(Enfermedad, Sintoma).
