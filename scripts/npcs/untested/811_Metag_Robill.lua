local npc = Npc(811, 9045)

npc.scaleX = 140
npc.scaleY = 140
npc.colors = {6908265, 3412229, 15922849}
npc.accessories = {7192, 6813, 772, 0, 7082}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
    if answer == 0 then p:ask(3364, {3007, 3008})
    elseif answer == 3008 then p:ask(3365, {3009})
    elseif answer == 3007 then p:endDialog()
    end
end

RegisterNPCDef(npc)