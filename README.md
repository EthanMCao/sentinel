# Sentinel

**Anti-Xray Detection Plugin for Minecraft Paper 1.21**

Sentinel is a sophisticated anti-cheat plugin that detects xray-like behavior by analyzing player mining patterns and tracking suspicious activity. It integrates with SIEM systems like Wazuh for centralized monitoring and alerting.

---

## Features

- **Three-Tier Detection System**
  - Pure Ore Mining: Detects players mining valuable ores with minimal stone
  - Low Stone Mining: Identifies unusually high ore-to-stone ratios
  - High Ratio Detection: Flags suspicious diamond-to-stone ratios after baseline mining

- **Real-Time Monitoring**
  - Tracks mining statistics per player (diamonds, ores, stone)
  - Accumulates suspicion scores based on detection patterns
  - Logs suspicious events to JSON for SIEM integration

- **Admin Tools**
  - `/sentinel stats <player>` - View mining statistics and suspicion score
  - `/sentinel reset <player>` - Reset player statistics
  - `/sentinel flag <player>` - Manually add suspicion points

- **Staff Exemption**
  - Automatically skips tracking for creative/spectator mode
  - Exempts OPs and players with `sentinel.admin` permission

- **Configurable Thresholds**
  - Customize detection sensitivity via `config.yml`
  - Adjust suspicion points for each detection type
  - Configure logging options (console, JSON, both)

- **SIEM Integration**
  - JSONL-formatted logs for Wazuh, ELK, or Splunk
  - Custom Wazuh rules and decoders included
  - Real-time dashboard for viewing suspicious activity

---

## Installation

### Prerequisites

- **Minecraft Server:** Paper 1.21 or higher
- **Java:** 21 or higher
- **Build Tool:** Gradle (included via wrapper)

### Build from Source

```bash
# Clone the repository
git clone https://github.com/EthanMCao/sentinel.git
cd sentinel

# Build the plugin
./gradlew clean build

# The JAR will be in: build/libs/Sentinel-1.0-SNAPSHOT.jar
```

### Install on Server

```bash
# Copy the JAR to your server's plugins folder
cp build/libs/Sentinel-1.0-SNAPSHOT.jar /path/to/minecraft-server/plugins/

# Restart your Minecraft server
```

---

## Configuration

The plugin creates `config.yml` in `plugins/Sentinel/` on first run.

### Detection Thresholds

```yaml
# Pure Ore Mining Detection
pure_ore_mining:
  enabled: true
  valuable_ores_threshold: 5
  stone_threshold: 20
  suspicion_points: 20

# Low Stone Mining Detection
low_stone_mining:
  enabled: true
  valuable_ores_threshold: 10
  stone_threshold: 50
  suspicion_points: 15

# High Ratio Detection
high_ratio:
  enabled: true
  stone_baseline: 100
  diamond_ratio_threshold: 0.15
  suspicion_points: 10
```

### Logging Options

```yaml
logging:
  console_enabled: true
  json_enabled: true
  json_path: "logs/antixray.jsonl"
```

---

## Commands

### `/sentinel stats <player>`
View mining statistics for a player.

**Permission:** `sentinel.admin`

**Example:**
```
/sentinel stats tofu123man

--- Mining Stats for tofu123man ---
Diamonds: 76
Stone: 275
Diamond/Stone Ratio: 0.276
Total Suspicion Score: 700
```

### `/sentinel reset <player>`
Reset all statistics for a player.

**Permission:** `sentinel.admin`

### `/sentinel flag <player>`
Manually add 50 suspicion points to a player.

**Permission:** `sentinel.admin`

---

## SIEM Integration

Sentinel outputs JSONL logs compatible with Wazuh, ELK Stack, Splunk, and other SIEM systems.

### Log Format

```json
{
  "ts": "2025-10-05T19:37:13.140229Z",
  "player": "tofu123man",
  "player_uuid": "f4b68850-f7c1-4cb8-9d52-0cfb3e8db56c",
  "event_type": "diamond_mine",
  "ore": "UNKNOWN",
  "location": {"x": 93, "y": 66, "z": 53, "world": "world"},
  "stone_mined": 275,
  "diamond_mined": 76,
  "total_ores_mined": 76,
  "diamond_ratio": 0.276,
  "suspicion_score": 700,
  "detection_type": "high_ratio",
  "reason": "Very high diamond ratio: 0.276 (diamonds: 76, stone: 275)"
}
```

### Wazuh Setup

See [WAZUH_SETUP.md](docs/WAZUH_SETUP.md) for detailed integration steps.

**Quick overview:**
1. Install Wazuh Agent on your Minecraft server machine
2. Configure agent to monitor `plugins/Sentinel/logs/antixray.jsonl`
3. Add custom decoders and rules to Wazuh Manager
4. Create dashboard visualizations

---

## Startup Guide

**Use this checklist when starting from everything off:**

### 1. Start Ubuntu VM (if using Wazuh)
```bash
# Verify Wazuh services running
sudo systemctl status wazuh-manager
sudo systemctl status wazuh-indexer
```

### 2. Start Wazuh Agent (on your Mac/server)
```bash
sudo /Library/Ossec/bin/wazuh-control start

# Verify agent running
sudo /Library/Ossec/bin/wazuh-control status
# Should show: wazuh-agentd is running...
```

### 3. Start Minecraft Server
```bash
cd ~/Documents/minecraft-server
./start.sh  # or your start command

# Verify Sentinel loaded
tail -50 logs/latest.log | grep Sentinel
# Should show: [Sentinel] Enabling Sentinel v1.0
```

### 4. Access Wazuh Dashboard (optional)
- Open browser: `https://YOUR_WAZUH_VM_IP`
- Go to your custom Sentinel dashboard
- Verify agent "mac-sentinel" shows as **Active**

### 5. Test the System
- Join Minecraft server
- Mine 10+ diamonds with minimal stone
- Wait 30-60 seconds
- Check Wazuh dashboard for detections

---

## Detection Methodology

### Pure Ore Mining
- **Trigger:** 5+ valuable ores with < 20 stone
- **Suspicion:** +20 points
- **Why:** Xrayers often mine only valuable blocks

### Low Stone Mining
- **Trigger:** 10+ valuable ores with < 50 stone
- **Suspicion:** +15 points
- **Why:** Indicates selective mining patterns

### High Ratio Detection
- **Trigger:** After 100+ stone, diamond ratio > 0.15
- **Suspicion:** +10 points per detection
- **Why:** Natural mining has ~0.05-0.08 diamond ratio

---

## Development

### Tech Stack
- **Language:** Java 21
- **Build System:** Gradle
- **Framework:** Paper API 1.21
- **Dependencies:** Gson 2.11.0

### Project Structure
```
Sentinel/
├── src/main/java/dev/ethancao/antixray/
│   ├── Sentinel.java                  # Main plugin class
│   ├── config/
│   │   └── ConfigManager.java         # Configuration handler
│   ├── tracking/
│   │   ├── MiningStats.java           # Player statistics
│   │   └── PlayerDataManager.java     # Stats management
│   └── logging/
│       ├── XrayEvent.java             # Event data model
│       └── JsonLogger.java            # JSONL writer
├── src/main/resources/
│   ├── plugin.yml                     # Plugin metadata
│   └── config.yml                     # Default configuration
└── build.gradle                       # Build configuration
```

### Building
```bash
./gradlew clean build
```

### Testing
```bash
# Copy to test server
cp build/libs/Sentinel-1.0-SNAPSHOT.jar ~/test-server/plugins/

# View logs
tail -f ~/test-server/plugins/Sentinel/logs/antixray.jsonl
```

---

## Troubleshooting

### Agent Shows "Disconnected" in Wazuh
```bash
# Restart agent
sudo /Library/Ossec/bin/wazuh-control restart
```

### No Logs Appearing in Dashboard
```bash
# Check if agent is monitoring the file
sudo tail -20 /Library/Ossec/logs/ossec.log | grep antixray
# Should show: Analyzing file: '.../antixray.jsonl'

# Watch logs arriving at VM
sudo tail -f /var/ossec/logs/alerts/alerts.json | grep '"id":"003"'
```

### Plugin Not Loading
```bash
# Check server logs for errors
tail -100 ~/Documents/minecraft-server/logs/latest.log | grep -i error
```

---

## Permissions

- `sentinel.admin` - Access to all `/sentinel` commands (default: op)

---

## License

This project is open source. Feel free to modify and distribute.

---

## Author

**EthanCao**  
GitHub: [@EthanMCao](https://github.com/EthanMCao)

---

## Contributing

Issues and pull requests welcome! Please ensure:
- Code follows existing style
- Changes are tested on Paper 1.21
- Commit messages are clear and descriptive

---

## Roadmap

- [ ] Persistence across server restarts (SQLite storage)
- [ ] Discord webhook notifications
- [ ] Auto-actions (kick/ban) on high suspicion
- [ ] Vein tracking for advanced detection
- [ ] Web-based admin dashboard
- [ ] Multi-language support

---

## Acknowledgments

- Paper Team for the excellent server software
- Wazuh for SIEM capabilities
- Minecraft community for feedback and testing

