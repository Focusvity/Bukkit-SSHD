BukkitSSH
===========
[![Build Status](https://travis-ci.org/Focusvity/BukkitSSH.svg?branch=master)](https://travis-ci.org/Focusvity/BukkitSSH)

## Compiling
`DIR` is your project directory. Recommended compiling method: Command Prompt (using `mvn` command).

1. Open your command prompt

2. Execute `cd DIR` (Ex: `cd C:/Users/user/link/to/BukkitSSH`)

3. Run `mvn clean install`

The jar will be located in `DIR/target`.

## For TotalFreedom
When generating a key, please use your minecraft username as the plugin's API will be using your minecraft username.

## Adding Public Key
Use `putty-gen` (If on Windows) to help you generate the public and private key.

After generating a key, save the public key under your name with no extension (Example: `Focusvity`).

Save your private key and store it somewhere safe.

Place the public key in `authorized_keys` folder in your server's `plugins` folder.

Log in using the details you have set up with the username you created with the public key.