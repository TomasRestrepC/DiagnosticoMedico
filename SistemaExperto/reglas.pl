% Declaramos que estos hechos serán dinámicos 
:- dynamic enfermedad/3.      % enfermedad(Nombre, Categoria, Recomendacion)
:- dynamic sintoma_de/2.      % sintoma_de(NombreEnfermedad, Sintoma)

% --- REGLAS (Lógica de diagnóstico) ---

% Regla 1: Verifica si una enfermedad tiene un síntoma específico
tiene_sintoma(Enf, Sintoma) :-
    sintoma_de(Enf, Sintoma).

% Regla 2: coincide_sintomas 
% Verifica si una lista de síntomas del paciente coincide con los de la enfermedad.
% Si coincide al menos uno, es un posible candidato.
coincide_sintomas(Enfermedad, ListaSintomasPaciente) :-
    enfermedad(Enfermedad, _, _),
    member(Sintoma, ListaSintomasPaciente),
    sintoma_de(Enfermedad, Sintoma).

% Regla 3: diagnostico (Devuelve enfermedad y datos)
diagnostico(Enfermedad, Categoria, Recomendacion, ListaSintomasPaciente) :-
    enfermedad(Enfermedad, Categoria, Recomendacion),
    coincide_sintomas(Enfermedad, ListaSintomasPaciente).

% Regla para limpiar la base de conocimiento antes de una nueva consulta
limpiar_conocimiento :-
    retractall(enfermedad(_,_,_)),
    retractall(sintoma_de(_,_)).