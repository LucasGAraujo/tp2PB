    package org.example.model;

    public final class User {

        private final long id;
        private final String name;
        private final String email;

        public User(String name, String email) {
            this(0, name, email);
        }

        public User(long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
        public User atualizar(String name, String email) {
            return new User(this.id, name, email);
        }
        public static User vazio() {
            return new User(0, "", "");
        }
    }
