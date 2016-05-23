package com.glitchcog.fontificator.gui.component.combomenu;

import javax.swing.JMenuItem;

import com.glitchcog.fontificator.game.GameGenre;
import com.glitchcog.fontificator.game.GameSystem;
import com.glitchcog.fontificator.game.VideoGame;

public class GameFontMenuItem extends JMenuItem
{
    private static final long serialVersionUID = 1L;

    private VideoGame game;

    public GameFontMenuItem(String text, VideoGame game)
    {
        super(text);
        this.game = game;
    }

    public VideoGame getGame()
    {
        return game;
    }

    public boolean isMatchingFilterGame(String filter)
    {
        filter = filter.toLowerCase().trim();
        return game.getName().toLowerCase().contains(filter);
    }

    public boolean isMatchingFilterSystem(String filter)
    {
        for (GameSystem system : GameSystem.values())
        {
            if (system.matchesName(filter))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isMatchingFilterGenre(String filter)
    {
        for (GameGenre genre : GameGenre.values())
        {
            if (genre.matchesName(filter))
            {
                return true;
            }
        }
        return false;
    }
}
