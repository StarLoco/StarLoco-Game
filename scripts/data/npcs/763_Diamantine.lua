local npc = Npc(763, 1357)

npc.gender = 1
npc.colors = {4048569, 4048569, 4048569}

---@param p Player
---@param answer number
function npc:onTalk(p, answer)
local hasAllItem = p:getItem(8077, 10) and p:getItem(8064, 10) and p:getItem(8075, 10) and p:getItem(8076, 10)
    if answer == 0 then 
        p:ask(3147, {2768})
    elseif answer == 2768 then 
	    if p:spellLevel(364) > 0 then
            p:ask(3148, {})
        elseif hasAllItem then
            p:ask(3148, {2769})
        else
            p:ask(3148, {})
        end
    elseif answer == 2769 then
        p:ask(3149, {2770})
    elseif answer == 2770 then
        if hasAllItem then
            p:consumeItem(8077, 10)
            p:consumeItem(8064, 10)
            p:consumeItem(8075, 10)
            p:consumeItem(8076, 10)
			p:setSpellLevel(364, 1)
            p:endDialog()
        else
            p:ask(3148, {})
        end
    end
end

RegisterNPCDef(npc)
