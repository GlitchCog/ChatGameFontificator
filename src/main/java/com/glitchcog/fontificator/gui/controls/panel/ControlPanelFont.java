package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.FontType;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.CharacterPicker;
import com.glitchcog.fontificator.gui.component.LabeledInput;
import com.glitchcog.fontificator.gui.component.LabeledSlider;
import com.glitchcog.fontificator.gui.component.combomenu.ComboMenuBar;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownBorder;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownFont;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownLabel;

/**
 * Control Panel containing all the Font and Border options
 * 
 * @author Matt Yanos
 */
public class ControlPanelFont extends ControlPanelBase
{
    private static final Logger logger = Logger.getLogger(ControlPanelFont.class);

    private static final long serialVersionUID = 1L;

    /**
     * Text of the selection option in the font and border preset dropdown menus to prompt the user to specify her own
     * file
     */
    private static final DropdownLabel CUSTOM_KEY = new DropdownLabel(null, "Custom...");

    private static final Color SCALE_EVEN = Color.BLACK;

    private static final Color SCALE_UNEVEN = new Color(0x661033);

    private static final Map<DropdownLabel, DropdownFont> PRESET_FONT_FILE_MAP = new LinkedHashMap<DropdownLabel, DropdownFont>()
    {
        private static final long serialVersionUID = 1L;

        {
            put(CUSTOM_KEY, null);
            put(new DropdownLabel("7th Dragon", "7th Dragon (Dialog)"), new DropdownFont("7d_dialog_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("7th Dragon", "7th Dragon (Name)"), new DropdownFont("7d_name_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("7th Saga", "7th Saga Battle"), new DropdownFont("7saga_battle_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Animal Crossing", "Animal Crossing"), new DropdownFont("ac_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Ape Escape", "Ape Escape Credits"), new DropdownFont("ape_esc_credits_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Bahamut Lagoon", "Bahamut Lagoon"), new DropdownFont("bah_lag_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Breath of Fire", "Breath of Fire"), new DropdownFont("bof1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Breath of Fire", "Breath of Fire 2"), new DropdownFont("bof2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Castlevania", "Castlevania 2 Title"), new DropdownFont("cv2_title_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Castlevania", "Castlevania 3"), new DropdownFont("cv3_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Castlevania", "Castlevania: Symphony of the Night"), new DropdownFont("csotn_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Chrono", "Chrono Trigger"), new DropdownFont("ct_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Chrono", "Chrono Cross"), new DropdownFont("cc_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Clash at Demonhead", "Clash at Demonhead"), new DropdownFont("cad_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Crystalis", "Crystalis"), new DropdownFont("crystalis_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Disney Capcom", "Chip 'n Dale Rescue Rangers"), new DropdownFont("cndrr_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Disney Capcom", "DuckTales"), new DropdownFont("ducktales_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Disney Capcom", "TaleSpin"), new DropdownFont("talespin_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Donkey Kong", "Donkey Kong 94"), new DropdownFont("dk94_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Donkey Kong", "Donkey Kong Country"), new DropdownFont("dkc_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Donkey Kong", "Donkey Kong Country Banana"), new DropdownFont("dkc_banana_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Donkey Kong", "Donkey Kong Country KONG"), new DropdownFont("dkc_kong_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior"), new DropdownFont("dw1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior II"), new DropdownFont("dw2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Quest I.II (SFC)"), new DropdownFont("dq1_2_sfc_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III"), new DropdownFont("dw3_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III (GBC) Dialog"), new DropdownFont("dw3gbc_dialog_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III (GBC) Fight"), new DropdownFont("dw3gbc_fight_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Quest III (SFC)"), new DropdownFont("dq3_sfc_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior IV"), new DropdownFont("dw4_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior VII Fight"), new DropdownFont("dw7_fight_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Quest Heroes: Rocket Slime Dialog"), new DropdownFont("dqhrs_dialog_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Drill Dozer", "Drill Dozer Dialog"), new DropdownFont("drilldozer_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("EarthBound", "EarthBound Zero"), new DropdownFont("eb0_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("EarthBound", "EarthBound Zero Bold"), new DropdownFont("eb0_bold_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("EarthBound", "EarthBound"), new DropdownFont("eb_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("EarthBound", "EarthBound Mr. Saturn"), new DropdownFont("eb_saturn_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("EarthBound", "Mother 3"), new DropdownFont("m3_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Faxanadu", "Faxanadu (PRG1) Dialog"), new DropdownFont("faxanadu_dialog_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Faxanadu", "Faxanadu (PRG0) Dialog and HUD"), new DropdownFont("faxanadu_hud_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy"), new DropdownFont("ff1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy II"), new DropdownFont("ff2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy Dawn of Souls"), new DropdownFont("ffdos_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy IV"), new DropdownFont("ff4_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy VI"), new DropdownFont("ff6_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy VI (Battle)"), new DropdownFont("ff6_battle_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy VII"), new DropdownFont("ff7_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy IX"), new DropdownFont("ff9_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy Tactics Advance (Dialog)"), new DropdownFont("ffta_dialog_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy Tactics Advance (Menu)"), new DropdownFont("ffta_menu_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Freedom Planet", "Freedom Planet"), new DropdownFont("freep_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Ghosts and Goblins", "Ghosts n Goblins"), new DropdownFont("gng_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Golden Sun", "Golden Sun"), new DropdownFont("gsun_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Golden Sun", "Golden Sun (Battle)"), new DropdownFont("gsun_battle_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Harvest Moon", "Friends of Mineral Town"), new DropdownFont("hm_fmt_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Harvest Moon", "Friends of Mineral Town Inverted"), new DropdownFont("hm_fmt_i_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Harvest Moon", "The Tale of Two Towns"), new DropdownFont("hm_ttott_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Harvest Moon", "The Tale of Two Towns Name"), new DropdownFont("hm_ttott_name_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Holy Diver", "Holy Diver"), new DropdownFont("holy_diver_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Kunio-kun", "River City Ransom"), new DropdownFont("rcr_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Lost Vikings", "The Lost Vikings"), new DropdownFont("lostvik_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Lufia", "Lufia II: Rise of the Sinistrals"), new DropdownFont("lufia2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros."), new DropdownFont("smb1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 2"), new DropdownFont("smb2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3"), new DropdownFont("smb3_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3 HUD"), new DropdownFont("smb3_hud_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3 HUD (Mixed Case)"), new DropdownFont("smb3_hud_lowercase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Land"), new DropdownFont("sml_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario World"), new DropdownFont("smw_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario World 2: Yoshi's Island"), new DropdownFont("yi_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario 64"), new DropdownFont("sm64_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario 64 Multicolor"), new DropdownFont("sm64_multicolor_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Mario", "Dr. Mario"), new DropdownFont("drmario_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Mario is Missing"), new DropdownFont("mmiss_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario RPG: Dark"), new DropdownFont("smrpg_dark_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario RPG: Light"), new DropdownFont("smrpg_light_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Mario", "Paper Mario: The Thousand Year Door"), new DropdownFont("pmttyd_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Mega Man", "Mega Man 9"), new DropdownFont("mm9_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mega Man", "Mega Man X"), new DropdownFont("mmx_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mega Man", "Mega Man Battle Network"), new DropdownFont("mmbn1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Metal Gear", "Metal Gear"), new DropdownFont("mg_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid"), new DropdownFont("metroid_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid II"), new DropdownFont("metroid2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid II (Credits)"), new DropdownFont("metroid2_credits_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Metroid", "Super Metroid"), new DropdownFont("smetroid_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Metroid", "Super Metroid (Mixed Case)"), new DropdownFont("smetroid_mixedcase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid Fusion"), new DropdownFont("metroid_fusion_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid Fusion Outline"), new DropdownFont("metroid_fusion_outline_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid Zero Mission"), new DropdownFont("metroid_zm_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid Zero Mission Outline"), new DropdownFont("metroid_zm_outline_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Moon Crystal", "Moon Crystal Dialog"), new DropdownFont("moon_crystal_dialog_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Moon Crystal", "Moon Crystal HUD"), new DropdownFont("moon_crystal_hud_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Ogre Battle", "Ogre Battle: The March of the Black Queen"), new DropdownFont("ogreb_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Phantasy Star", "Phantasy Star"), new DropdownFont("ps1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Phantasy Star", "Phantasy Star 2"), new DropdownFont("ps2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Pokemon", "Pokemon Red/Blue"), new DropdownFont("pkmnrb_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Pokemon", "Pokemon Fire Red/Leaf Green"), new DropdownFont("pkmnfrlg_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Princess Tomato", "Princess Tomato in the Salad Kingdom"), new DropdownFont("ptsk_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Quintet", "Robotrek"), new DropdownFont("robotrek_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Quintet", "Robotrek (Battle)"), new DropdownFont("robotrek_battle_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Quintet", "Terranigma"), new DropdownFont("terranigma_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Rygar", "Rygar (NES)"), new DropdownFont("rygar_nes_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Secret of Evermore", "Secret of Evermore"), new DropdownFont("soe_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Shadow Hearts", "Shadow Hearts: Covenant Cutscene"), new DropdownFont("shadhearts2_cutscene_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Shantae", "Shantae"), new DropdownFont("shantae_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Shining", "Shining Force Dialog"), new DropdownFont("shining_force_dialog_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Shining", "Shining Force Menu"), new DropdownFont("shining_force_menu_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Shovel Knight", "Shovel Knight"), new DropdownFont("sk_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Simpsons", "Bart vs. the Space Mutants"), new DropdownFont("bart_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Solstice", "Solstice"), new DropdownFont("sol_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Sonic", "Sega System"), new DropdownFont("sega_sys_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Sonic", "Sonic Team"), new DropdownFont("sonic_team_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Stardew Valley", "Stardew Valley"), new DropdownFont("sdv_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Star Ocean", "Star Ocean Dialog"), new DropdownFont("staroc_dialog_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Star Ocean", "Star Ocean HUD"), new DropdownFont("staroc_hud_font.png", FontType.FIXED_WIDTH));
            // put(new DropdownLabel("Star Ocean", "Star Ocean HUD"), new DropdownFont("staroc_font.png", FontType.FIXED_WIDTH)); // Same as staroc_hud_font, retired, but font file left in resources
            put(new DropdownLabel("Star Ocean", "Star Ocean: The Second Story HUD"), new DropdownFont("staroc2_hud_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Studio Pixel", "Cave Story+"), new DropdownFont("cavestoryplus_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Studio Pixel", "Kero Blaster"), new DropdownFont("keroblast_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Suikoden", "Suikoden"), new DropdownFont("suiko_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Tales", "Tales of Phantasia HUD (SFC, Mixed Case)"), new DropdownFont("tophan_hud_sfc_mixedcase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Tales", "Tales of Phantasia HUD (SFC, Upper Case)"), new DropdownFont("tophan_hud_sfc_uppercase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Tales", "Tales of Symphonia"), new DropdownFont("tos_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Tengai Makyou Zero", "Tengai Makyou Zero"), new DropdownFont("tmzero_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Tetris", "Tetris (GB)"), new DropdownFont("tetris_gb_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Tetris", "Tetris (NES)"), new DropdownFont("tetris_nes_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("TMNT", "Teenage Mutant Ninja Turtles (NES)"), new DropdownFont("tmnt_nes_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Umihara Kawase", "Umihara Kawase"), new DropdownFont("umi_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Undertale", "Undertale"), new DropdownFont("undertale_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Wario", "Wario Land 4 (Dark)"), new DropdownFont("wl4_dark_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Wario", "Wario Land 4 (Light)"), new DropdownFont("wl4_light_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Wild Arms", "Wild Arms"), new DropdownFont("wildarms_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Willow", "Willow (NES)"), new DropdownFont("willow_nes_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Wonder Boy", "Wonder Boy In Monster World"), new DropdownFont("wbmw_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Ys", "Ys (NES)"), new DropdownFont("ys1_fc_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Ys", "Ys III (NES)"), new DropdownFont("ys3_fc_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Ys", "Ys III (SNES)"), new DropdownFont("ys3_snes_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda"), new DropdownFont("loz_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda (Mixed Case)"), new DropdownFont("loz_lowercase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "Zelda II: The Adventures of Link"), new DropdownFont("zelda2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "Zelda II (Mixed Case)"), new DropdownFont("zelda2_lowercase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: A Link to the Past"), new DropdownFont("lttp_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: Link's Awakening"), new DropdownFont("loz_la_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: Ocarina of Time"), new DropdownFont("loz_oot_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: The Wind Waker"), new DropdownFont("loz_ww_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: The Minish Cap"), new DropdownFont("minish_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: Skyward Sword"), new DropdownFont("loz_ss_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Zero Wing", "Zero Wing"), new DropdownFont("zw_font.png", FontType.FIXED_WIDTH));
        }
    };

    /**
     * A reverse lookup through the map to get the actual name of a game for a font
     * 
     * @param font
     *            filename
     * @return game name
     */
    public static String getFontGameName(String filename)
    {
        for (Entry<DropdownLabel, DropdownFont> e : PRESET_FONT_FILE_MAP.entrySet())
        {
            if (CUSTOM_KEY.equals(e.getKey()))
            {
                continue;
            }

            final String fontFilename = e.getValue().getFontFilename();

            if (fontFilename.equals(filename))
            {
                return e.getKey().getLabel();
            }
        }
        return "Unknown";
    }

    public static List<DropdownFont> getAllFonts()
    {
        List<DropdownFont> allFonts = new ArrayList<DropdownFont>();
        for (DropdownLabel ddfKey : PRESET_FONT_FILE_MAP.keySet())
        {
            if (CUSTOM_KEY.equals(ddfKey))
            {
                continue;
            }
            allFonts.add(PRESET_FONT_FILE_MAP.get(ddfKey));
        }
        return allFonts;
    }

    private static final Map<DropdownLabel, DropdownBorder> PRESET_BORDER_FILE_MAP = new LinkedHashMap<DropdownLabel, DropdownBorder>()
    {
        private static final long serialVersionUID = 1L;

        {
            put(CUSTOM_KEY, null);
            put(new DropdownLabel("7th Dragon", "7th Dragon (Left)"), new DropdownBorder("7d_left_border.png", 0x208053));
            put(new DropdownLabel("7th Dragon", "7th Dragon (Right)"), new DropdownBorder("7d_right_border.png", 0x208053));
            put(new DropdownLabel("Breath of Fire", "Breath of Fire"), new DropdownBorder("bof1_border.png", 0xE7E7E7));
            put(new DropdownLabel("Breath of Fire", "Breath of Fire 2"), new DropdownBorder("bof2_border.png", 0xD6C6E7));
            put(new DropdownLabel("Castlevania", "Castlevania 2"), new DropdownBorder("cv2_border.png", Color.WHITE));
            put(new DropdownLabel("Castlevania", "Castlevania 3"), new DropdownBorder("cv3_border.png", Color.WHITE));
            put(new DropdownLabel("Castlevania", "Castlevania: Symphony of the Night Name"), new DropdownBorder("csotn_name_border.png", Color.WHITE));
            put(new DropdownLabel("Castlevania", "Castlevania: Symphony of the Night Enemy"), new DropdownBorder("csotn_enemy_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger"), new DropdownBorder("ct_border.png", 0xCEDBE8));
            put(new DropdownLabel("Chrono", "Chrono Trigger 1 (Unshaded)"), new DropdownBorder("ct_border_1.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger 2 (Unshaded)"), new DropdownBorder("ct_border_2.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger 3 (Unshaded)"), new DropdownBorder("ct_border_3.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger 4 (Unshaded)"), new DropdownBorder("ct_border_4.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger 5 (Unshaded)"), new DropdownBorder("ct_border_5.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger 6 (Unshaded)"), new DropdownBorder("ct_border_6.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger 7 (Unshaded)"), new DropdownBorder("ct_border_7.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Trigger 8 (Unshaded)"), new DropdownBorder("ct_border_8.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross"), new DropdownBorder("cc_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Menu"), new DropdownBorder("cc_menu_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Arnian Wood"), new DropdownBorder("cc_arnian_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Simple Line"), new DropdownBorder("cc_simple_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Iron Plate"), new DropdownBorder("cc_iron_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Shellfish"), new DropdownBorder("cc_shellfish_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Tea for Three"), new DropdownBorder("cc_tea_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Guldovian Stitch"), new DropdownBorder("cc_guldovian_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Infrared Vision"), new DropdownBorder("cc_infrared_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Valencian Cloth"), new DropdownBorder("cc_valencian_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Monster's Mouth"), new DropdownBorder("cc_monster_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Our Favorite Martian"), new DropdownBorder("cc_martian_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Snakes & Orbs"), new DropdownBorder("cc_snake_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Porre's Furnace"), new DropdownBorder("cc_furnace_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Skullduggery"), new DropdownBorder("cc_skull_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Tropical Paradise"), new DropdownBorder("cc_tropics_border.png", Color.WHITE));
            put(new DropdownLabel("Chrono", "Chrono Cross Quill and Papyrus"), new DropdownBorder("cc_papyrus_border.png", Color.WHITE));
            put(new DropdownLabel("Clash at Demonhead", "Clash at Demonhead"), new DropdownBorder("cad_border.png", Color.WHITE));
            put(new DropdownLabel("Clash at Demonhead", "Clash at Demonhead Hermit"), new DropdownBorder("cad_hermit_border.png", Color.WHITE));
            put(new DropdownLabel("Clash at Demonhead", "Clash at Demonhead Shop"), new DropdownBorder("cad_shop_border.png", 0x44009C));
            put(new DropdownLabel("Clash at Demonhead", "Clash at Demonhead Suzie"), new DropdownBorder("cad_suzie_border.png", Color.WHITE));
            put(new DropdownLabel("Cyrstalis", "Crystalis"), new DropdownBorder("crystalis_border.png", Color.WHITE));
            put(new DropdownLabel("Disney Capcom", "Chip 'n Dale Rescue Rangers Zone Clear"), new DropdownBorder("cndrr_zone_border.png", Color.WHITE));
            put(new DropdownLabel("Disney Capcom", "DuckTales HUD"), new DropdownBorder("ducktales_hud_border.png", Color.WHITE));
            put(new DropdownLabel("Disney Capcom", "DuckTales Land Select"), new DropdownBorder("ducktales_land_border.png", Color.WHITE));
            put(new DropdownLabel("Disney Capcom", "TaleSpin"), new DropdownBorder("talespin_border.png", Color.WHITE));
            put(new DropdownLabel("Donkey Kong", "Donkey Kong '94"), new DropdownBorder("dk94_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior"), new DropdownBorder("dw1_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior II"), new DropdownBorder("dw2_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Quest I.II (SFC)"), new DropdownBorder("dq1_2_sfc_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III"), new DropdownBorder("dw3_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III (GBC)"), new DropdownBorder("dw3gbc_border.png", Color.BLACK));
            put(new DropdownLabel("Dragon Warrior", "Dragon Quest III (SFC)"), new DropdownBorder("dq3_sfc_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior IV"), new DropdownBorder("dw4_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Quest VI (SFC)"), new DropdownBorder("dq6_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior VII"), new DropdownBorder("dw7_border.png", Color.WHITE));
            put(new DropdownLabel("Dragon Warrior", "Dragon Quest Heroes: Rocket Slime"), new DropdownBorder("dqhrs_border.png", Color.WHITE));
            put(new DropdownLabel("Drill Dozer", "Drill Dozer"), new DropdownBorder("drilldozer_border.png", Color.WHITE));
            put(new DropdownLabel("EarthBound", "EarthBound Zero"), new DropdownBorder("eb0_border.png", Color.WHITE));
            put(new DropdownLabel("EarthBound", "EarthBound Plain"), new DropdownBorder("eb_plain.png", Color.WHITE));
            put(new DropdownLabel("EarthBound", "EarthBound Mint"), new DropdownBorder("eb_mint.png", Color.WHITE));
            put(new DropdownLabel("EarthBound", "EarthBound Strawberry"), new DropdownBorder("eb_strawberry.png", Color.WHITE));
            put(new DropdownLabel("EarthBound", "EarthBound Banana"), new DropdownBorder("eb_banana.png", Color.WHITE));
            put(new DropdownLabel("EarthBound", "EarthBound Peanut"), new DropdownBorder("eb_peanut.png", Color.WHITE));
            put(new DropdownLabel("EarthBound", "Mother 3"), new DropdownBorder("m3_border.png", Color.WHITE));
            put(new DropdownLabel("Faxanadu", "Faxanadu"), new DropdownBorder("faxanadu_border.png", Color.WHITE));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy"), new DropdownBorder("ff1_filled_border.png", Color.WHITE));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy Frame"), new DropdownBorder("ff1_border.png", Color.WHITE));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy IV"), new DropdownBorder("ff4_border.png", Color.WHITE));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy VI"), new DropdownBorder("ff6_border.png", Color.WHITE));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy VII"), new DropdownBorder("ff7_border.png", Color.WHITE));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy IX"), new DropdownBorder("ff9_border.png", Color.WHITE));
            put(new DropdownLabel("Freedom Planet", "Freedom Planet"), new DropdownBorder("freep_border.png", Color.WHITE));
            put(new DropdownLabel("Golden Sun", "Golden Sun"), new DropdownBorder("gsun_border.png", Color.WHITE));
            put(new DropdownLabel("Harvest Moon", "Friends of Mineral Town"), new DropdownBorder("hm_fmt_border.png", Color.WHITE));
            put(new DropdownLabel("Harvest Moon", "Friends of Mineral Town Transparent"), new DropdownBorder("hm_fmt_t_border.png", Color.WHITE));
            put(new DropdownLabel("Harvest Moon", "The Tale of Two Towns"), new DropdownBorder("hm_ttott_border.png", Color.WHITE));
            put(new DropdownLabel("Harvest Moon", "The Tale of Two Towns Blue"), new DropdownBorder("hm_ttott_blue_border.png", Color.WHITE));
            put(new DropdownLabel("Harvest Moon", "The Tale of Two Towns Green"), new DropdownBorder("hm_ttott_green_border.png", Color.WHITE));
            put(new DropdownLabel("Harvest Moon", "The Tale of Two Towns Yellow"), new DropdownBorder("hm_ttott_yellow_border.png", Color.WHITE));
            put(new DropdownLabel("Kunio-kun", "River City Ransom"), new DropdownBorder("rcr_border.png", Color.WHITE));
            put(new DropdownLabel("Lost Vikings", "The Lost Vikings"), new DropdownBorder("lostvik_border.png", Color.WHITE));
            put(new DropdownLabel("Lufia", "Lufia II: Rise of the Sinistrals Speech"), new DropdownBorder("lufia2_border.png", Color.WHITE));
            put(new DropdownLabel("Lufia", "Lufia II: Rise of the Sinistrals Thought"), new DropdownBorder("lufia2_thought_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. Brick"), new DropdownBorder("smb1_brick_border.png", 0x994E00));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. Cloud"), new DropdownBorder("smb1_cloud_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. Rock"), new DropdownBorder("smb1_rock_border.png", 0x994E00));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. Seabed"), new DropdownBorder("smb1_seabed_border.png", 0x00A800));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. Stone"), new DropdownBorder("smb1_stone_border.png", 0xBCBCBC));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. Empty Block"), new DropdownBorder("smb1_used_border.png", 0xA06400));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. 2 Pause"), new DropdownBorder("smb2_pause_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. 3 HUD"), new DropdownBorder("smb3_hud_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (NES)", "Super Mario Bros. 3 Letter"), new DropdownBorder("smb3_letter_border.png", 0xFBA7C3));
            put(new DropdownLabel("Mario (NES)", "Dr. Mario"), new DropdownBorder("drmario_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Block"), new DropdownBorder("smw_block_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Bonus"), new DropdownBorder("smw_bonus_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Gold"), new DropdownBorder("smw_gold_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Ground"), new DropdownBorder("smw_ground_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Mesh"), new DropdownBorder("smw_mesh_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Pipe"), new DropdownBorder("smw_pipe_border.png", 0x00F800));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Rock"), new DropdownBorder("smw_rock_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World Yoshi's House"), new DropdownBorder("smw_yoshi_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario World 2: Yoshi's Island"), new DropdownBorder("yi_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario RPG"), new DropdownBorder("smrpg_border.png", Color.WHITE));
            put(new DropdownLabel("Mario (SNES)", "Super Mario RPG Pipes"), new DropdownBorder("smrpg_pipe_border.png", 0x31EF4A));
            put(new DropdownLabel("Mega Man", "Mega Man 9 Stage Select"), new DropdownBorder("mm9_stage_border.png", Color.WHITE));
            put(new DropdownLabel("Mega Man", "Mega Man 9 Menu"), new DropdownBorder("mm9_menu_border.png", Color.WHITE));
            put(new DropdownLabel("Mega Man", "Mega Man 9 Menu Popup"), new DropdownBorder("mm9_popup_border.png", Color.WHITE));
            put(new DropdownLabel("Mega Man", "Mega Man X"), new DropdownBorder("mmx_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Metroid"), new DropdownBorder("metroid_border.png", 0x4090C0));
            put(new DropdownLabel("Metroid", "Metroid II"), new DropdownBorder("metroid2_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Metroid Pipes"), new DropdownBorder("metroid_pipe_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Metroid Mother Brain Glass"), new DropdownBorder("metroid_glass_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Super Metroid Mother Inventory"), new DropdownBorder("smetroid_inventory_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Super Metroid Broken Glass"), new DropdownBorder("smetroid_glass_broke_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Super Metroid Glass"), new DropdownBorder("smetroid_glass_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Metroid Fusion"), new DropdownBorder("metroid_fusion_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Metroid Fusion Frame"), new DropdownBorder("metroid_fusion_frame_border.png", Color.WHITE));
            put(new DropdownLabel("Metroid", "Metroid Zero Mission"), new DropdownBorder("metroid_zm_border.png", Color.WHITE));
            put(new DropdownLabel("Moon Crystal", "Moon Crystal"), new DropdownBorder("moon_crystal_border.png", Color.WHITE));
            put(new DropdownLabel("Ogre Battle", "Ogre Battle: The March of the Black Queen"), new DropdownBorder("ogreb_border.png", Color.WHITE));
            put(new DropdownLabel("Phantasy Star", "Phantasy Star"), new DropdownBorder("ps1_border.png", Color.WHITE));
            put(new DropdownLabel("Phantasy Star", "Phantasy Star 2"), new DropdownBorder("ps2_border.png", Color.WHITE));
            put(new DropdownLabel("Pokemon", "Pokemon Red/Blue"), new DropdownBorder("pkmnrb_border.png", 0x171717));
            put(new DropdownLabel("Pokemon", "Pokemon Fire Red/Leaf Green"), new DropdownBorder("pkmnfrlg_border.png", 0x66CCFF));
            put(new DropdownLabel("Pokemon", "Pokemon Ruby/Sapphire"), new DropdownBorder("pkmnrubysaph_border.png", Color.WHITE));
            put(new DropdownLabel("Princess Tomato", "Princess Tomato in the Salad Kingdom"), new DropdownBorder("ptsk_border.png", Color.WHITE));
            put(new DropdownLabel("Quintet", "Robotrek"), new DropdownBorder("robotrek_border.png", Color.WHITE));
            put(new DropdownLabel("Quintet", "Robotrek (Battle)"), new DropdownBorder("robotrek_battle_border.png", Color.WHITE));
            put(new DropdownLabel("Quintet", "Terranigma"), new DropdownBorder("terranigma_border.png", Color.WHITE));
            put(new DropdownLabel("Rygar", "Rygar (NES)"), new DropdownBorder("rygar_nes_border.png", Color.WHITE));
            put(new DropdownLabel("Secret of Evermore", "Secret of Evermore"), new DropdownBorder("soe_border.png", 0xCEDBE8));
            put(new DropdownLabel("Shantae", "Shantae"), new DropdownBorder("shantae_border.png", Color.WHITE));
            put(new DropdownLabel("Shining", "Shining Force"), new DropdownBorder("shining_force_border.png", Color.WHITE));
            put(new DropdownLabel("Shovel Knight", "Shovel Knight"), new DropdownBorder("sk_border.png", Color.WHITE));
            put(new DropdownLabel("Stardew Valley", "Stardew Valley Dialog"), new DropdownBorder("sdv_dialog_border.png", Color.WHITE));
            put(new DropdownLabel("Stardew Valley", "Stardew Valley Portrait"), new DropdownBorder("sdv_portrait_border.png", Color.WHITE));
            put(new DropdownLabel("Stardew Valley", "Stardew Valley Scroll"), new DropdownBorder("sdv_scroll_border.png", Color.WHITE));
            put(new DropdownLabel("Star Ocean", "Star Ocean Dialog"), new DropdownBorder("staroc_dialog_border.png", Color.WHITE));
            put(new DropdownLabel("Star Ocean", "Star Ocean Dialog Color"), new DropdownBorder("staroc_dialog_color_border.png", Color.WHITE));
            put(new DropdownLabel("Star Ocean", "Star Ocean HUD"), new DropdownBorder("staroc_hud_border.png", Color.WHITE));
            // put(new DropdownLabel("Star Ocean", "Star Ocean HUD"), new DropdownBorder("staroc_border.png", Color.WHITE)); // Same as staroc_hud_border, retired, but border file left in resources
            put(new DropdownLabel("Star Ocean", "Star Ocean HUD Color"), new DropdownBorder("staroc_hud_color_border.png", Color.WHITE));
            put(new DropdownLabel("Star Ocean", "Star Ocean: The Second Story"), new DropdownBorder("staroc2_border.png", Color.WHITE));
            put(new DropdownLabel("Suikoden", "Suikoden"), new DropdownBorder("suiko_border.png", Color.WHITE));
            put(new DropdownLabel("Tales", "Tales of Phantasia (SFC)"), new DropdownBorder("tophan_sfc_border.png", Color.WHITE));
            put(new DropdownLabel("Tales", "Tales of Symphonia B"), new DropdownBorder("tos_b_border.png", Color.WHITE));
            put(new DropdownLabel("Tales", "Tales of Symphonia C"), new DropdownBorder("tos_c_border.png", Color.WHITE));
            put(new DropdownLabel("Tengai Makyou Zero", "Tengai Makyou Zero"), new DropdownBorder("tmzero_border.png", Color.WHITE));
            put(new DropdownLabel("Tetris", "Tetris Next (GB)"), new DropdownBorder("tetris_gb_border.png", Color.WHITE));
            put(new DropdownLabel("Tetris", "Tetris (NES)"), new DropdownBorder("tetris_nes_border.png", Color.WHITE));
            put(new DropdownLabel("Tetris", "Tetris Next (NES)"), new DropdownBorder("tetris_nes_next_border.png", Color.WHITE));
            put(new DropdownLabel("TMNT", "Teenage Mutant Ninja Turtles Dialog (NES)"), new DropdownBorder("tmnt_nes_dialog_border.png", Color.WHITE));
            put(new DropdownLabel("TMNT", "Teenage Mutant Ninja Turtles Map (NES)"), new DropdownBorder("tmnt_nes_map_border.png", Color.WHITE));
            put(new DropdownLabel("TMNT", "Teenage Mutant Ninja Turtles Portraits Color (NES)"), new DropdownBorder("tmnt_nes_portraits_color_border.png", Color.WHITE));
            put(new DropdownLabel("TMNT", "Teenage Mutant Ninja Turtles Portraits Gray (NES)"), new DropdownBorder("tmnt_nes_portraits_gray_border.png", Color.WHITE));
            put(new DropdownLabel("Umihara Kawase", "Umihara Kawase"), new DropdownBorder("umi_border.png", Color.WHITE));
            put(new DropdownLabel("Undertale", "Undertale"), new DropdownBorder("undertale_border.png", Color.WHITE));
            put(new DropdownLabel("Wario", "Wario Land 4"), new DropdownBorder("wl4_border.png", 0x58F8F8));
            put(new DropdownLabel("Wild Arms", "Wild Arms"), new DropdownBorder("wildarms_border.png", Color.WHITE));
            put(new DropdownLabel("Willow", "Willow (NES)"), new DropdownBorder("willow_nes_border.png", 0xFC9838));
            put(new DropdownLabel("Wonder Boy", "Wonder Boy In Monster World"), new DropdownBorder("wbmw_border.png", 0xE7E3E7));
            put(new DropdownLabel("Ys", "Ys Dialog (FC)"), new DropdownBorder("ys1_fc_dialog_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys Menu (FC)"), new DropdownBorder("ys1_fc_menu_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys II Dialog (FC)"), new DropdownBorder("ys2_fc_dialog_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys II HUD (FC)"), new DropdownBorder("ys2_fc_hud_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys II Menu (FC)"), new DropdownBorder("ys2_fc_menu_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys II Portrait (FC)"), new DropdownBorder("ys2_fc_portrait_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys III Dialog (FC)"), new DropdownBorder("ys3_fc_dialog_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys III HUD (FC)"), new DropdownBorder("ys3_fc_hud_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys III (SNES)"), new DropdownBorder("ys3_snes_border.png", Color.WHITE));
            put(new DropdownLabel("Ys", "Ys III Frame (SNES)"), new DropdownBorder("ys3_snes_frame_border.png", Color.WHITE));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Bush"), new DropdownBorder("loz_bush_border.png", 0x48A810));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Rock"), new DropdownBorder("loz_rock_border.png", 0xC84C0C));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Dungeon"), new DropdownBorder("loz_dungeon_border.png", Color.WHITE));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Story"), new DropdownBorder("loz_story_border.png", 0x80D010));
            put(new DropdownLabel("Zelda", "Zelda II: The Adventures of Link"), new DropdownBorder("zelda2_border.png", Color.WHITE));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: A Link to the Past"), new DropdownBorder("lttp_border.png", Color.WHITE));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: Link's Awakening Room"), new DropdownBorder("loz_la_room_border.png", 0xBFC79F));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: Link's Awakening Name"), new DropdownBorder("loz_la_name_border.png", 0xBFC79F));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: The Wind Waker"), new DropdownBorder("loz_ww_border.png", Color.WHITE));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: The Minish Cap Dialog"), new DropdownBorder("minish_dialog_border.png", Color.WHITE));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: The Minish Cap Select"), new DropdownBorder("minish_select_border.png", Color.WHITE));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: The Minish Cap Stone"), new DropdownBorder("minish_stone_border.png", 0xE0D8D8));
        }
    };

    /**
     * A reverse lookup through the map to get the actual name of a game for a border
     * 
     * @param border
     *            filename
     * @return game name
     */
    public static String getBorderGameName(String filename)
    {
        for (Entry<DropdownLabel, DropdownBorder> e : PRESET_BORDER_FILE_MAP.entrySet())
        {
            if (CUSTOM_KEY.equals(e.getKey()))
            {
                continue;
            }

            final String borderFilename = e.getValue().getBorderFilename();

            if (borderFilename.equals(filename))
            {
                return e.getKey().getLabel();
            }
        }
        return "Unknown";
    }

    private LabeledInput fontFilenameInput;

    private ComboMenuBar fontPresetDropdown;

    private JCheckBox fontTypeCheckbox;

    private LabeledInput borderFilenameInput;

    private ComboMenuBar borderPresetDropdown;

    private LabeledInput gridWidthInput;

    private LabeledInput gridHeightInput;

    private JFileChooser fontPngPicker;

    private JFileChooser borderPngPicker;

    private LabeledSlider fontScaleSlider;

    private LabeledSlider borderScaleSlider;

    private LabeledSlider borderInsetXSlider;

    private LabeledSlider borderInsetYSlider;

    private LabeledInput characterKeyInput;

    private LabeledSlider spaceWidthSlider;

    private LabeledSlider baselineOffsetSlider;

    private LabeledSlider lineSpacingSlider;

    private LabeledSlider charSpacingSlider;

    private JButton unknownCharPopupButton;

    private CharacterPicker charPicker;

    private JLabel unknownCharLabel;

    private JCheckBox extendedCharBox;

    private ConfigFont config;

    private ChangeListener sliderListener;

    private ActionListener fontTypeListener;

    private ControlPanelColor colorPanel;

    /**
     * Construct a font control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param logBox
     * @param colorPanel
     *            Used to set the border color tint when the border is changed
     */
    public ControlPanelFont(FontificatorProperties fProps, ChatWindow chatWindow, LogBox logBox, ControlPanelColor colorPanel)
    {
        super("Font/Border", fProps, chatWindow, logBox);

        this.colorPanel = colorPanel;

        fontTypeCheckbox.addActionListener(fontTypeListener);

        fontPngPicker = new JFileChooser();
        borderPngPicker = new JFileChooser();
        FileFilter pngFilter = new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory() || (f.getName().toLowerCase().endsWith(".png"));
            }

            @Override
            public String getDescription()
            {
                return "PNG Image (*.png)";
            }
        };
        fontPngPicker.setFileFilter(pngFilter);
        borderPngPicker.setFileFilter(pngFilter);
    }

    @Override
    protected void build()
    {
        sliderListener = new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                JSlider source = (JSlider) e.getSource();
                if (fontScaleSlider.getSlider().equals(source))
                {
                    config.setFontScale(fontScaleSlider.getScaledValue());
                    fontScaleSlider.setValueTextColor(((int) config.getFontScale() == config.getFontScale()) ? SCALE_EVEN : SCALE_UNEVEN);
                }
                else if (borderScaleSlider.getSlider().equals(source))
                {
                    config.setBorderScale(borderScaleSlider.getScaledValue());
                    borderScaleSlider.setValueTextColor(((int) config.getBorderScale() == config.getBorderScale()) ? SCALE_EVEN : SCALE_UNEVEN);
                }
                else if (borderInsetXSlider.getSlider().equals(source))
                {
                    config.setBorderInsetX(borderInsetXSlider.getValue());
                }
                else if (borderInsetYSlider.getSlider().equals(source))
                {
                    config.setBorderInsetY(borderInsetYSlider.getValue());
                }
                else if (spaceWidthSlider.getSlider().equals(source))
                {
                    config.setSpaceWidth(spaceWidthSlider.getValue());
                }
                else if (baselineOffsetSlider.getSlider().equals(source))
                {
                    config.setBaselineOffset(baselineOffsetSlider.getValue());
                }
                else if (lineSpacingSlider.getSlider().equals(source))
                {
                    config.setLineSpacing(lineSpacingSlider.getValue());
                }
                else if (charSpacingSlider.getSlider().equals(source))
                {
                    config.setCharSpacing(charSpacingSlider.getValue());
                }
                chat.repaint();
            }
        };

        ActionListener fontAl = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JMenuItem source = (JMenuItem) e.getSource();
                DropdownLabel key = new DropdownLabel(source.getText());
                if (CUSTOM_KEY.equals(key))
                {
                    int selectionResult = fontPngPicker.showDialog(ControlWindow.me, "Select Font PNG");
                    if (selectionResult == JFileChooser.APPROVE_OPTION)
                    {
                        fontFilenameInput.setText(fontPngPicker.getSelectedFile().getAbsolutePath());
                        fontPresetDropdown.setSelectedText(fontPngPicker.getSelectedFile().getName());
                    }
                }
                else
                {
                    fontFilenameInput.setText(PRESET_FONT_FILE_MAP.get(key).getFontFilename());
                    fontTypeCheckbox.setSelected(FontType.VARIABLE_WIDTH.equals(PRESET_FONT_FILE_MAP.get(key).getDefaultType()));
                    spaceWidthSlider.setEnabled(fontTypeCheckbox.isSelected());
                }
                updateFontOrBorder(true);
            }
        };

        ActionListener borderAl = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JMenuItem source = (JMenuItem) e.getSource();
                DropdownLabel key = new DropdownLabel(source.getText());
                if (CUSTOM_KEY.equals(key))
                {
                    int selectionResult = borderPngPicker.showDialog(ControlWindow.me, "Select Border PNG");
                    if (selectionResult == JFileChooser.APPROVE_OPTION)
                    {
                        borderFilenameInput.setText(borderPngPicker.getSelectedFile().getAbsolutePath());
                        borderPresetDropdown.setSelectedText(borderPngPicker.getSelectedFile().getName());
                    }
                }
                else
                {
                    DropdownBorder border = PRESET_BORDER_FILE_MAP.get(key);
                    borderFilenameInput.setText(border.getBorderFilename());
                    colorPanel.setBorderColor(border.getDefaultTint());
                }
                updateFontOrBorder(false);
            }
        };

        fontTypeListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                config.setFontType(fontTypeCheckbox.isSelected() ? FontType.VARIABLE_WIDTH : FontType.FIXED_WIDTH);
                spaceWidthSlider.setEnabled(fontTypeCheckbox.isSelected());
                updateFontOrBorder(true);
            }
        };

        extendedCharBox = new JCheckBox("Display Extended Characters");
        extendedCharBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final boolean ecbSelected = extendedCharBox.isSelected();

                config.setExtendedCharEnabled(ecbSelected);
                unknownCharPopupButton.setEnabled(!ecbSelected);
                unknownCharLabel.setEnabled(!ecbSelected);
                chat.repaint();
            }
        });

        unknownCharLabel = new JLabel("");
        charPicker = new CharacterPicker(ControlWindow.me, fProps.getFontConfig(), unknownCharLabel, chat);

        Map<String, List<String>> fontMenuMap = getMenuMapFromPresets(PRESET_FONT_FILE_MAP.keySet());
        fontMenuMap.put(CUSTOM_KEY.getLabel(), null);
        Map<String, List<String>> borderMenuMap = getMenuMapFromPresets(PRESET_BORDER_FILE_MAP.keySet());
        borderMenuMap.put(CUSTOM_KEY.getLabel(), null);

        fontTypeCheckbox = new JCheckBox("Variable Width Characters");
        fontFilenameInput = new LabeledInput("Font Filename", 32);
        fontPresetDropdown = new ComboMenuBar(fontMenuMap, fontAl);
        borderFilenameInput = new LabeledInput("Border Filename", 32);
        borderPresetDropdown = new ComboMenuBar(borderMenuMap, borderAl);
        gridWidthInput = new LabeledInput("Grid Width", 4);
        gridHeightInput = new LabeledInput("Grid Height", 4);
        fontScaleSlider = new LabeledSlider("Font Size", "x", ConfigFont.MIN_FONT_SCALE, ConfigFont.MAX_FONT_SCALE, ConfigFont.FONT_BORDER_SCALE_GRANULARITY);
        borderScaleSlider = new LabeledSlider("Border Size", "x", ConfigFont.MIN_BORDER_SCALE, ConfigFont.MAX_BORDER_SCALE, ConfigFont.FONT_BORDER_SCALE_GRANULARITY);
        borderInsetXSlider = new LabeledSlider("X", "pixels", ConfigFont.MIN_BORDER_INSET, ConfigFont.MAX_BORDER_INSET);
        borderInsetYSlider = new LabeledSlider("Y", "pixels", ConfigFont.MIN_BORDER_INSET, ConfigFont.MAX_BORDER_INSET);
        characterKeyInput = new LabeledInput("Character Key", 32);
        spaceWidthSlider = new LabeledSlider("Space Width", "%", ConfigFont.MIN_SPACE_WIDTH, ConfigFont.MAX_SPACE_WIDTH);
        baselineOffsetSlider = new LabeledSlider("Baseline Height Offset", "pixels", ConfigFont.MIN_BASELINE_OFFSET, ConfigFont.MAX_BASELINE_OFFSET);
        lineSpacingSlider = new LabeledSlider("Line Spacing", "pixels", ConfigFont.MIN_LINE_SPACING, ConfigFont.MAX_LINE_SPACING);
        charSpacingSlider = new LabeledSlider("Char Spacing", "pixels", ConfigFont.MIN_CHAR_SPACING, ConfigFont.MAX_LINE_SPACING);
        unknownCharPopupButton = new JButton("Select Missing Character");

        unknownCharPopupButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                charPicker.setVisible(true);
            }
        });

        fontFilenameInput.setEnabled(false);
        borderFilenameInput.setEnabled(false);

        fontScaleSlider.addChangeListener(sliderListener);
        borderScaleSlider.addChangeListener(sliderListener);
        borderInsetXSlider.addChangeListener(sliderListener);
        borderInsetYSlider.addChangeListener(sliderListener);
        spaceWidthSlider.addChangeListener(sliderListener);
        baselineOffsetSlider.addChangeListener(sliderListener);
        lineSpacingSlider.addChangeListener(sliderListener);
        charSpacingSlider.addChangeListener(sliderListener);

        JPanel fontPanel = new JPanel(new GridBagLayout());
        JPanel borderPanel = new JPanel(new GridBagLayout());
        JPanel unknownPanel = new JPanel(new GridBagLayout());

        fontPanel.setBorder(new TitledBorder(baseBorder, "Font"));
        borderPanel.setBorder(new TitledBorder(baseBorder, "Border"));
        unknownPanel.setBorder(new TitledBorder(baseBorder, "Extended and Unicode Characters"));

        GridBagConstraints fontGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);
        GridBagConstraints borderGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);
        GridBagConstraints unknownGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);

        // Fields are still used and stored in properties files when saved, but the values are either fixed or meant to
        // be filled by other components
        // add(fontFilenameInput);
        // add(borderFilenameInput);
        // add(gridWidthInput);
        // add(gridHeightInput);
        // add(characterKeyInput);

        fontPanel.add(fontPresetDropdown, fontGbc);
        fontGbc.gridx++;
        // This slider being on the same row as the preset dropdown keeps the combo menu bar from collapsing to no
        // height in the layout
        fontPanel.add(fontScaleSlider, fontGbc);
        fontGbc.gridwidth = 2;
        fontGbc.gridx = 0;
        fontGbc.gridy++;

        fontPanel.add(lineSpacingSlider, fontGbc);
        fontGbc.gridy++;
        fontPanel.add(charSpacingSlider, fontGbc);
        fontGbc.gridy++;

        JPanel variableWidthPanel = new JPanel(new GridBagLayout());
        GridBagConstraints vwpGbc = getGbc();
        vwpGbc.anchor = GridBagConstraints.EAST;
        vwpGbc.weightx = 0.0;
        vwpGbc.fill = GridBagConstraints.NONE;
        variableWidthPanel.add(fontTypeCheckbox, vwpGbc);
        vwpGbc.anchor = GridBagConstraints.WEST;
        vwpGbc.weightx = 1.0;
        vwpGbc.fill = GridBagConstraints.HORIZONTAL;
        vwpGbc.gridx++;
        variableWidthPanel.add(spaceWidthSlider, vwpGbc);
        fontPanel.add(variableWidthPanel, fontGbc);
        fontGbc.gridy++;

        fontPanel.add(baselineOffsetSlider, fontGbc);
        fontGbc.gridy++;

        borderPanel.add(borderPresetDropdown, borderGbc);
        borderGbc.gridx++;
        // This slider being on the same row as the preset dropdown keeps the combo menu bar from collapsing to no
        // height in the layout
        borderPanel.add(borderScaleSlider, borderGbc);
        borderGbc.gridwidth = 2;
        borderGbc.gridx = 0;
        borderGbc.gridy++;

        borderGbc.anchor = GridBagConstraints.CENTER;

        borderPanel.add(new JLabel("Font Insets Off Border"), borderGbc);
        borderGbc.gridy++;
        borderPanel.add(borderInsetXSlider, borderGbc);
        borderGbc.gridy++;
        borderPanel.add(borderInsetYSlider, borderGbc);
        borderGbc.gridy++;

        unknownPanel.add(extendedCharBox, unknownGbc);
        unknownGbc.gridx++;
        unknownPanel.add(unknownCharPopupButton, unknownGbc);
        unknownGbc.gridx++;
        unknownPanel.add(unknownCharLabel, unknownGbc);
        unknownGbc.gridx++;

        JPanel everything = new JPanel(new GridBagLayout());
        GridBagConstraints eGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 10, 10);
        everything.add(fontPanel, eGbc);
        eGbc.gridy++;
        everything.add(borderPanel, eGbc);
        eGbc.gridy++;
        everything.add(unknownPanel, eGbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(everything, gbc);

        // Filler panel
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(new JPanel(), gbc);
    }

    /**
     * Turns a list of keys with a single pipe deliminating the parent menu from the menu item into a map that can be a
     * parameter to create a ComboMenuBar
     * 
     * @param keys
     * @return
     */
    private Map<String, List<String>> getMenuMapFromPresets(Collection<DropdownLabel> keys)
    {
        Map<String, List<String>> menuMap = new LinkedHashMap<String, List<String>>();
        for (DropdownLabel key : keys)
        {
            final String menu = key.getGroup();
            final String item = key.getLabel();
            List<String> items = menuMap.get(menu);
            if (items == null)
            {
                items = new ArrayList<String>();
                menuMap.put(menu, items);
            }
            items.add(item);
        }
        return menuMap;
    }

    private boolean updateFontOrBorder(boolean isFont)
    {
        LoadConfigReport report = new LoadConfigReport();
        if (isFont)
        {
            config.validateFontFile(report, fontFilenameInput.getText());
            config.validateStrings(report, gridWidthInput.getText(), gridHeightInput.getText(), characterKeyInput.getText(), Character.toString(charPicker.getSelectedChar()));
        }
        else
        {
            config.validateBorderFile(report, borderFilenameInput.getText());
        }

        if (report.isErrorFree())
        {
            try
            {
                fillConfigFromInput();
                if (isFont)
                {
                    chat.reloadFontFromConfig();
                }
                else
                {
                    chat.reloadBorderFromConfig();
                }
                chat.repaint();
                return true;
            }
            catch (Exception ex)
            {
                logger.error(ex.toString(), ex);
                ChatWindow.popup.handleProblem("Unable to load " + (isFont ? "font" : "border") + " image", ex);
            }
        }
        else
        {
            ChatWindow.popup.handleProblem(report);
        }
        return false;
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        this.config = fProps.getFontConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        borderFilenameInput.setText(config.getBorderFilename());
        fontFilenameInput.setText(config.getFontFilename());
        gridWidthInput.setText(Integer.toString(config.getGridWidth()));
        gridHeightInput.setText(Integer.toString(config.getGridHeight()));
        fontScaleSlider.setScaledValue(config.getFontScale());
        borderScaleSlider.setScaledValue(config.getBorderScale());
        borderInsetXSlider.setValue(config.getBorderInsetX());
        borderInsetYSlider.setValue(config.getBorderInsetY());
        characterKeyInput.setText(config.getCharacterKey());
        extendedCharBox.setSelected(config.isExtendedCharEnabled());
        charPicker.setSelectedChar(config.getUnknownChar());
        spaceWidthSlider.setValue(config.getSpaceWidth());
        baselineOffsetSlider.setValue(config.getBaselineOffset());
        lineSpacingSlider.setValue(config.getLineSpacing());
        charSpacingSlider.setValue(config.getCharSpacing());
        fontTypeCheckbox.setSelected(FontType.VARIABLE_WIDTH.equals(config.getFontType()));
        spaceWidthSlider.setEnabled(fontTypeCheckbox.isSelected());
        final boolean ecbSelected = extendedCharBox.isSelected();
        unknownCharPopupButton.setEnabled(!ecbSelected);
        unknownCharLabel.setEnabled(!ecbSelected);

        boolean found;

        found = false;
        for (Map.Entry<DropdownLabel, DropdownFont> entry : PRESET_FONT_FILE_MAP.entrySet())
        {
            if (entry.getValue() != null && config.getFontFilename().equals(entry.getValue().getFontFilename()))
            {
                found = true;
                fontPresetDropdown.setSelectedText(entry.getKey().getLabel());
            }
        }
        if (!found)
        {
            String filename = new File(fontFilenameInput.getText()).getName();
            fontPresetDropdown.setSelectedText(filename);
        }

        found = false;
        for (Map.Entry<DropdownLabel, DropdownBorder> entry : PRESET_BORDER_FILE_MAP.entrySet())
        {
            if (entry.getValue() != null && config.getBorderFilename().equals(entry.getValue().getBorderFilename()))
            {
                found = true;
                borderPresetDropdown.setSelectedText(entry.getKey().getLabel());
            }
        }
        if (!found)
        {
            String filename = new File(borderFilenameInput.getText()).getName();
            borderPresetDropdown.setSelectedText(filename);
        }

        // Although the font was already updated from the listener attached the the fontTypeDropdown, it should be done
        // here to make it official. If the font and border aren't updated, they could be out of sync with the input
        // filled from config on preset loads, and it shouldn't be the responsibility of actionlisteners attached to UI
        // components to update the display
        updateFontOrBorder(true);

        // Also, the border must be updated here too.
        updateFontOrBorder(false);
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        LoadConfigReport report = new LoadConfigReport();

        config.validateFontFile(report, fontFilenameInput.getText());
        config.validateBorderFile(report, borderFilenameInput.getText());

        final String widthStr = gridWidthInput.getText();
        final String heightStr = gridHeightInput.getText();
        final String charKey = characterKeyInput.getText();
        final String unknownStr = Character.toString(charPicker.getSelectedChar());

        config.validateStrings(report, widthStr, heightStr, charKey, unknownStr);

        return report;
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setBorderFilename(borderFilenameInput.getText());
        config.setFontFilename(fontFilenameInput.getText());
        config.setFontType(fontTypeCheckbox.isSelected() ? FontType.VARIABLE_WIDTH : FontType.FIXED_WIDTH);
        config.setGridWidth(Integer.parseInt(gridWidthInput.getText()));
        config.setGridHeight(Integer.parseInt(gridHeightInput.getText()));
        config.setFontScale(fontScaleSlider.getScaledValue());
        config.setBorderScale(borderScaleSlider.getScaledValue());
        config.setBorderInsetX(borderInsetXSlider.getValue());
        config.setBorderInsetY(borderInsetYSlider.getValue());
        config.setCharacterKey(characterKeyInput.getText());
        config.setExtendedCharEnabled(extendedCharBox.isSelected());
        config.setUnknownChar(charPicker.getSelectedChar());
        config.setSpaceWidth(spaceWidthSlider.getValue());
        config.setBaselineOffset(baselineOffsetSlider.getValue());
        config.setLineSpacing(lineSpacingSlider.getValue());
        config.setCharSpacing(charSpacingSlider.getValue());
    }

}
