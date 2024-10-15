# CarpetSuppresionTools
A better and more functional version of the old CarpetSuppresion

## Functions
#### Book Requirement Calculator
A tool for calculating the amount of books needed for to compleat memory and reach OOM

command: `bookcalc memory base entityCount`
- `memory`: the total memory of the server in GB or MB (less than 512 is treated as GB)
- `base`: the amount of memory the server is currently using aproximately
- `entityCount`: the amount of entities your setup uses (0 if not using entityOOM)

#### Snowball Requirement Calculator
A tool for calculating the amount max amount of snowballs (or entities) posible for an entity OOM in a given server

command: `snowballcalc memory base theory`
- `memory`: the total memory of the server in GB or MB (less than 512 is treated as GB)
- `base`: the amount of memory the server is currently using aproximately
- `theory`: asume no entities already loaded or count currently loaded entitites

#### Entity UUID Count
A display similar to tps that counts the amount of entities at the same time in a given dimensions (even if in hiden chunks)

command: `log EntityUUID`

## TODO List
#### Dev
- [x] Upsize set function
- [ ] Add constant calculations
#### User
- [x] Command for amount of books needed -> Needs testing to set constant correctly
- [x] Snowball amount calc
- [ ] Time out suppression (Not geting kicked)
- [ ] OOM Help command 
    - [ ] Sub Help Pages for common debuging
- [ ] Creative tools for OOM
    - [X] Snowball Counter
    - [X] Creative OOM Block
    - [ ] Setup Command
      - [ ] Instant Snowballs
      - [ ] Instant Book Chunk
- [ ] Help for SyncUpdates OOM ???
