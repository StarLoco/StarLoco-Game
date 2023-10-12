local npc = Npc(811, 9045)
--TODO: Lié à la quête 152
npc.scaleX = 120
npc.scaleY = 120
npc.colors = {6908265, 3412229, 15922849}
npc.accessories = {7192, 6813, 772, 0, 7082}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3364, {3007, 3008})
    elseif answer == 3008 then p:ask(3365, {3009})
    elseif answer == 3009 then p:ask(3366, {3010, 3011})
    elseif answer == 3010 then p:endDialog()
    elseif answer == 3011 then p:endDialog()
    elseif answer == 3007 then p:endDialog()
    end
end

RegisterNPCDef(npc)
