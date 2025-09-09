# EasyShopGUI - Premium Minecraft Shop Plugin

![Version](https://img.shields.io/badge/version-1.4.5-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.16.x--1.21.x-green.svg)
![Java](https://img.shields.io/badge/java-17+-orange.svg)
![License](https://img.shields.io/badge/license-MIT-red.svg)

**EasyShopGUI** is a premium, feature-rich Minecraft shop plugin designed to provide the ultimate shopping experience for your server. With advanced features, stunning GUIs, Bedrock compatibility, and extensive customization options, it's the perfect solution for any server looking to implement a professional shop system.

## 🌟 Features

### ✅ Core Features
- **GUI Editor** - Edit items in-game using an intuitive GUI
- **NBT Items Support** - Full support for custom items from other plugins
- **Command Items** - Attach multiple commands to shop items
- **Delayed Commands** - Execute commands days/weeks/months after purchase
- **Limited Stock** - Configurable stock limits with automatic restocking
- **Limited Sell Items** - Control how many times items can be sold
- **Built-in Cron Schedulers** - Fixed restock intervals using cron schedules
- **Direct Item Support** - Use items from supported plugins as materials
- **Rotating Shops** - Display random items every X period
- **Per Item Currencies** - Different currencies per item
- **Item Economy** - Buy/sell using in-game items
- **Levels Economy** - Purchase using player levels
- **Dynamic Pricing** - Prices change based on supply and demand

### 🔧 Advanced Features
- **Per Item Permissions** - Custom permissions for specific items
- **PlaceholderAPI Integration** - Full placeholder support
- **Custom Enchants Support** - Works with major enchant plugins
- **Items Requirements** - Requirement system for purchases
- **Spawner Support** - EpicSpawners & UpgradeableSpawners integration
- **Skull Textures** - Custom skull texture support
- **MySQL Support** - Multi-server synchronization
- **Search System** - Convenient item search GUI
- **Update Command** - In-game plugin updates

### 🎨 Customization
- **Completely Customizable** - Every aspect can be customized
- **In-game Management** - Add/edit/remove shops without config files
- **In-game Reload** - No server restart required
- **Premade Layouts** - Browse marketplace for shop layouts
- **Unlimited Pages** - Add as many items as you want
- **Discounts & Multipliers** - Permission-based pricing
- **Multiple Currencies** - Different currencies per shop
- **Seasonal Pricing** - Price modifiers for different seasons

### 🎮 Bedrock Compatibility
- **Full Bedrock Support** - Works perfectly with Bedrock/MCPE players
- **No Inventory Glitches** - Doesn't interfere with vanilla inventories
- **Optimized Performance** - Reduced lag and improved responsiveness
- **Cross-Platform** - Seamless experience for Java and Bedrock players

### 🔌 Integrations
- **Vault Economy** - Support for all major economy plugins
- **PlaceholderAPI** - Custom placeholders and expansions
- **DiscordSRV** - Live transaction logging to Discord
- **Citizens** - NPC shop integration
- **Custom Enchants** - EcoEnchants, AdvancedEnchantments, etc.
- **Spawner Plugins** - EpicSpawners, UpgradeableSpawners

### 🌍 Multi-Version Support
- **Minecraft 1.8.x - 1.21.x** - Full compatibility across versions
- **Bedrock Support** - Developed with Bedrock players in mind
- **19+ Languages** - Pre-translated language files

## 🚀 Installation

### Prerequisites
- Java 17 or higher
- Minecraft Server (Spigot/Paper recommended)
- Vault plugin
- Economy plugin (EssentialsX, CMI, etc.)

### Building from Source
1. Clone the repository:
   ```bash
   git clone https://github.com/turjo/EasyShopGUI.git
   cd EasyShopGUI
   ```

2. Build with Maven:
   ```bash
   mvn clean package
   ```

3. The compiled JAR will be in the `target/` directory

### Installation Steps
1. Download the latest release from the releases page
2. Place the JAR file in your server's `plugins/` directory
3. Install Vault and an economy plugin if not already installed
4. Start your server
5. Configure the plugin in `plugins/EasyShopGUI/config.yml`
6. Restart your server or use `/eshop reload`

## 📖 Usage

### Basic Commands
- `/shop` - Open the main shop GUI
- `/shop [shopname]` - Open a specific shop
- `/eshop reload` - Reload configuration (Admin)
- `/eshop update` - Check for updates (Admin)
- `/eshop info` - Show plugin information (Admin)

### Permissions
- `easyshopgui.use` - Basic shop access (default: true)
- `easyshopgui.admin` - Admin commands (default: op)
- `easyshopgui.bypass` - Bypass all restrictions (default: op)
- `easyshopgui.shop.*` - Access to all shops
- `easyshopgui.discount.*` - All discount permissions
- `easyshopgui.multiplier.*` - All sell multiplier permissions

## ⚙️ Configuration

The plugin comes with extensive configuration options:

### Main Configuration (`config.yml`)
```yaml
# Plugin Settings
plugin:
  check-updates: true
  metrics: true
  debug: false
  language: "en_US"

# Database Configuration
database:
  type: "SQLITE"  # or MYSQL
  mysql:
    host: "localhost"
    port: 3306
    database: "easyshopgui"
    username: "root"
    password: "password"

# Economy Settings
economy:
  currency-symbol: "$"
  decimal-places: 2
  use-vault: true
```

### Shop Configuration
Shops can be configured through in-game GUIs or configuration files. The plugin supports:
- Custom shop layouts
- Item-specific settings
- Permission requirements
- Stock management
- Dynamic pricing

## 🔧 Development

### Project Structure
```
src/
├── main/
│   ├── java/dev/turjo/easyshopgui/
│   │   ├── EasyShopGUI.java          # Main plugin class
│   │   ├── commands/                  # Command handlers
│   │   ├── config/                    # Configuration management
│   │   ├── database/                  # Database operations
│   │   ├── economy/                   # Economy integration
│   │   ├── gui/                       # GUI management
│   │   ├── hooks/                     # Plugin integrations
│   │   ├── listeners/                 # Event listeners
│   │   ├── managers/                  # Core managers
│   │   ├── placeholders/              # PlaceholderAPI
│   │   ├── schedulers/                # Task scheduling
│   │   └── utils/                     # Utility classes
│   └── resources/
│       ├── plugin.yml                 # Plugin metadata
│       ├── config.yml                 # Main configuration
│       └── messages.yml               # Message templates
└── test/                              # Unit tests
```

### Contributing
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

### Building
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package plugin
mvn package

# Install to local repository
mvn install
```

## 📊 Performance

EasyShopGUI is designed with performance in mind:
- **Async Operations** - Database and file operations are asynchronous
- **Caching System** - Intelligent caching reduces database queries
- **Memory Optimization** - Efficient memory usage and garbage collection
- **Multi-threading** - CPU-intensive tasks use separate threads

## 🐛 Troubleshooting

### Common Issues
1. **Plugin won't load**
   - Check Java version (17+ required)
   - Verify Vault is installed
   - Check console for error messages

2. **Economy not working**
   - Install an economy plugin (EssentialsX recommended)
   - Verify Vault can detect the economy plugin

3. **Database errors**
   - Check database configuration
   - Verify MySQL credentials if using MySQL
   - Check file permissions for SQLite

### Debug Mode
Enable debug mode in `config.yml`:
```yaml
plugin:
  debug: true
```

This will provide detailed logging information to help diagnose issues.

## 📞 Support

- **Documentation**: [Wiki](https://github.com/turjo/EasyShopGUI/wiki)
- **Issues**: [GitHub Issues](https://github.com/turjo/EasyShopGUI/issues)
- **Discord**: [Support Server](https://discord.gg/easyshopgui)
- **Email**: support@easyshopgui.dev

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spigot Team** - For the excellent Minecraft server software
- **Vault Developers** - For the economy API
- **PlaceholderAPI Team** - For the placeholder system
- **Community Contributors** - For feedback and suggestions

## 🔮 Roadmap

### Version 1.1.0
- [ ] Web-based shop editor
- [ ] Advanced analytics dashboard
- [ ] Mobile app integration
- [ ] AI-powered pricing suggestions

### Version 1.2.0
- [ ] Auction house integration
- [ ] Player-to-player trading
- [ ] Advanced permission system
- [ ] Custom currency creation

---

**Made with ❤️ by Turjo**

*EasyShopGUI - The Ultimate Minecraft Shop Solution*