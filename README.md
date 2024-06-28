# Compressor - Minecraft Block Compression Plugin

![Build Status](https://github.com/Solidsilver/Compressor/actions/workflows/gradle.yml/badge.svg)

Compressor optimizes block storage in your Minecraft world by allowing you to compress specific types of blocks into more compact forms. This is achieved through crafting x grids of the same block, with the ability to compress up to 32 times. Compressed blocks can be uncrafted back to their original state.

## Features

* **Block Compression:** Craft a 3x3 grid of the same block type to create a single compressed block.
* **Multiple Compression Levels:** Compress blocks up to 32 times, each level requiring a 3x3 grid of the previous level's compressed block.
* **Decompression:** Uncraft compressed blocks back into 9 of their previous level's block (or the original block if it's the first level).

## Installation

1. **Prerequisites:** Ensure your server is running Paper version 1.20.4.
2. **Download:** Get the latest `Compressor-<VERSION>.jar` file from the [releases page]([your-github-release-page-url](https://github.com/Solidsilver/Compressor/releases)).
3. **Installation:** Place the JAR file in your server's `plugins` folder.
4. **Restart:** Restart your server to load the plugin.

## Usage

1. **Compression:**
    - Place a 3x3 grid of the desired block in a crafting table.
    - The result will be a compressed block of level 1.
    - Repeat this process with level 1 compressed blocks to create level 2, and so on.
2. **Decompression:**
    - Place a compressed block alone in a crafting table to retrieve 9 blocks of the previous compression level.
3. **Command:**
    - `compr <block> <level> (amount)` command for giving yourself a compressed block
    - `compressor.give` permission for the above command

## Supported Blocks

* Cobblestone
* Cobbled Deepslate
* Gold Block
* Stone
* Quartz Block
* Iron Block
* Diorite
* Andesite

## Building from Source

1. **Clone:** Clone this repository: `git clone https://github.com/Solidsilver/Compressor.git`
2. **Build:** Use Gradle to build: `./gradlew build`
3. **Jar:** The compiled plugin JAR will be in the `build/libs` folder.

## Contributing

Contributions are welcome! I will do my best to respond to issues and PRs in a timely manner.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
