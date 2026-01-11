# API UBS - Instituição Educacional
API REST em Spring Boot para gerenciar alunos e professores, com persistência de dados em arquivo utilizando o banco de dados H2 e integração com a API ViaCEP.

## Recursos principais

- CRUD: Operações completas para Alunos e Professores;
- Integração ViaCEP: Busca e complementação de endereço ao criar e atualizar usuários por meio do CEP;
- Tratamento de erros: Exceções customizadas (CPF duplicado, recurso não encontrado etc) e tratador global de excessões;
- Testes funcionais: Testes que utilizam um banco de dados H2 espelho daquele utilizado pela aplicação, mas em memória; ViaCep é mockado para testes.
- Documentação em Swagger: O Swagger foi utilizado para documentar os endpoints da aplicação facilitando o desenvolvimento e testes.

# Instruções

## Requisitos
Instalar JDK 21 e Maven.

## Como executar o projeto?
Abra a pasta raiz do projeto no terminal e execute o seguinte comando:

```
mvn spring-boot:run
```

### Como testar a API?
Abra o Swagger no navegador e interaja com os endpoints. 

http://localhost:8080/swagger-ui/index.html#/

**Obs:** Exemplos de uso foram fornecidos para cada uma das requisições!

## Como Executar os Testes?
Abra a pasta raiz do projeto no terminal e execute o seguinte comando:

```
mvn test
```
