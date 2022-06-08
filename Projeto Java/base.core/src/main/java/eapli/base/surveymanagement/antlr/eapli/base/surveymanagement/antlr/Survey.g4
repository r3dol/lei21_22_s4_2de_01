grammar Survey;

start: questionario;

alfanumerico: PALAVRA
             | NUMERO;

pontucao: PONTO_FINAL
         | PONTO_INTERROGACAO
         | RETICENCIAS
         | PONTO_EXCLAMACAO;

frase : ( PALAVRA | NUMERO )+ (VIRGULA? ESPACO (PALAVRA| NUMERO )+)*;

regraIdQuestionario: alfanumerico+ HIFEN alfanumerico+;

regraTitulo: frase;

regraMensagem: (frase pontucao NEWLINE)+ ;

regraId: NUMERO+;

obrigatoriedade: MANDATORY
            | OPTIONAL
            | CONDITION_DEPENDENT DOIS_PONTOS ESPACO frase;

repetibilidade : NUMERO+ ;

opcao: regraId PARENTESIS_DIREITO frase (DOIS_PONTOS)? NEWLINE;

type: FREE_TEXT NEWLINE
 | NUMERIC (ESPACO PARENTESIS_ESQUERDO DECIMALS_ALLOWED PARENTESIS_DIREITO)? NEWLINE
 | SINGLE_CHOICE NEWLINE (opcao)+
 | MULTIPLE_CHOICE NEWLINE (opcao)+
 | SINGLE_CHOICE1 NEWLINE (opcao)+
 | MULTIPLE_CHOICE1 NEWLINE (opcao)+
 | SORTING_OPTIONS NEWLINE (opcao)+
 | SCALING_OPTIONS NEWLINE 'Scale: ' frase NEWLINE (opcao)+
 ;

regraPergunta: frase PONTO_INTERROGACAO NEWLINE;

pergunta: regraId NEWLINE regraPergunta PARENTESIS_ESQUERDO obrigatoriedade PARENTESIS_DIREITO  (NEWLINE regraMensagem)? NEWLINE 'Type: ' type NEWLINE regraMensagem;

seccao: regraId NEWLINE regraTitulo NEWLINE regraMensagem? 'Section Obligatoriness: ' obrigatoriedade (NEWLINE 'Repeatability: ' repetibilidade)? NEWLINE pergunta+;

questionario: regraIdQuestionario ESPACO regraTitulo NEWLINE (regraMensagem)? (NEWLINE seccao)+ NEWLINE NEWLINE regraMensagem;

//------------------- TOKENS -------------------

MANDATORY: 'mandatory';
OPTIONAL: 'optional';
CONDITION_DEPENDENT: 'condition dependent';

FREE_TEXT: 'Free-Text';
NUMERIC: 'Numeric';
SINGLE_CHOICE:'Single-Choice';
SINGLE_CHOICE1: 'Single-Choice with input value';
MULTIPLE_CHOICE:'Multiple-Choice';
MULTIPLE_CHOICE1: 'Multiple-Choice with input value';
SORTING_OPTIONS:'Sorting Options';
SCALING_OPTIONS:'Scaling Options';

DECIMALS_ALLOWED: 'Decimal numbers are allowed!';

NUMERO: [0-9];
PALAVRA: [a-zA-Z]+;

HIFEN: '-';
ESPACO: ' '|'\t';
PONTO_FINAL: '.';
DOIS_PONTOS: ':';
RETICENCIAS: '...';
VIRGULA: ',';
PONTO_INTERROGACAO: '?';
PONTO_EXCLAMACAO: '!';
PARENTESIS_DIREITO: ')';
PARENTESIS_ESQUERDO: '(';
NEWLINE : [\r\n] ;
