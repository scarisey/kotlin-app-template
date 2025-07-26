{
  description = "Kotlin App template";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-24.11";
  };

  outputs = {
    self,
    nixpkgs,
  }: let
    javaVersion = 21; # Change this value to update the whole stack
    supportedSystems = ["x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin"];
    forEachSupportedSystem = f:
      nixpkgs.lib.genAttrs supportedSystems (system:
        f {
          pkgs = import nixpkgs {
            inherit system;
            overlays = [self.overlays.default];
          };
        });
  in {
    overlays.default = final: prev: let
      jdk = prev."jdk${toString javaVersion}";
    in {
      inherit jdk;
      gradle = prev.gradle.override {java = jdk;};
    };
    devShells = forEachSupportedSystem ({pkgs}: let
      deps = pkgs:
        with pkgs; [
          jdk
          gcc
          gradle
          ncurses
          patchelf
          zlib
        ];
    in {
      fhs =
        (pkgs.buildFHSUserEnv {
          name = "dev-default";
          targetPkgs = deps;
          runScript = "zsh";
          profile = ''
            export JAVA_HOME=${pkgs.jdk}/bin
            export PATH=''${PATH}:${pkgs.jdk}/bin
          '';
        })
        .env;
      default = pkgs.mkShell {
          packages = deps pkgs ++ [ pkgs.zsh ];
          shellHook = ''
            export JAVA_HOME=${pkgs.jdk}/bin
            export PATH=''${PATH}:${pkgs.jdk}/bin
          '';
      };
    });

    templates.default = { path = ./.; description="Kotlin App template";};
    formatter = forEachSupportedSystem ({pkgs}: pkgs.alejandra);
  };
}
