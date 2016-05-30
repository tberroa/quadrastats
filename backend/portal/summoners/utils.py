from summoners.models import Summoner

def create_summoner(region, name, profile_icon):
    try:
        summoner = Summoner.objects.get(region=region, name=name)
        summoner.profile_icon = profile_icon
        summoner.save()
        return summoner
    except Summoner.DoesNotExist:
        pass

    summoner = Summoner.objects.create(region=region, name=name, profile_icon=profile_icon)
    return summoner
